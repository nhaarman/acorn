package com.nhaarman.bravo.android.transition

import com.nhaarman.bravo.android.presentation.ViewFactory
import com.nhaarman.bravo.navigation.TransitionData
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.presentation.SceneKey

class SimpleTransitionFactory(
    private val viewFactory: ViewFactory,
    private val transitions: Map<Pair<SceneKey, SceneKey>, Transition>,
    private val classTransitions: Map<Pair<Class<out Scene<*>>, Class<out Scene<*>>>, Transition>,
    private val classTransitions2: MutableMap<Pair<Class<out Scene<*>>, Class<out Scene<*>>>, (Scene<*>) -> Transition>
) : TransitionFactory {

    private val delegate by lazy { DefaultTransitionFactory(viewFactory) }

    override fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): Transition {
        return transitions[previousScene.key to newScene.key]
            ?: classTransitions[previousScene::class.java to newScene::class.java]
            ?: classTransitions2[previousScene::class.java to newScene::class.java]?.invoke(newScene)
            ?: delegate.transitionFor(previousScene, newScene, data)
    }
}

class DefaultTransitionFactory(
    private val viewFactory: ViewFactory
) : TransitionFactory {

    override fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): Transition {
        return when (data?.isBackwards) {
            true -> FadeOutToBottomTransition { parent -> viewFactory.viewFor(newScene.key, parent) }
            else -> FadeInFromBottomTransition { parent -> viewFactory.viewFor(newScene.key, parent) }
        }
    }
}

fun transitionFactory(viewFactory: ViewFactory, init: TransitionFactoryBuilder.() -> Unit): TransitionFactory {
    return TransitionFactoryBuilder(viewFactory).apply(init).build()
}

class TransitionFactoryBuilder internal constructor(
    private val viewFactory: ViewFactory
) {

    private val keyTransitions = mutableMapOf<Pair<SceneKey, SceneKey>, Transition>()
    private val classTransitions = mutableMapOf<Pair<Class<out Scene<*>>, Class<out Scene<*>>>, Transition>()
    private val classTransitions2 =
        mutableMapOf<Pair<Class<out Scene<*>>, Class<out Scene<*>>>, (Scene<*>) -> Transition>()

    @JvmName("useWithClasses")
    infix fun Pair<Class<out Scene<*>>, Class<out Scene<*>>>.use(transition: Transition) {
        classTransitions += Pair(this, transition)
    }

    infix fun Pair<Class<out Scene<*>>, Class<out Scene<*>>>.use(transition: (Scene<*>) -> Transition) {
        classTransitions2 += Pair(this, transition)
    }

    infix fun Pair<SceneKey, SceneKey>.use(transition: Transition) {
        keyTransitions += Pair(this, transition)
    }

    fun build() = SimpleTransitionFactory(
        viewFactory,
        keyTransitions,
        classTransitions,
        classTransitions2
    )
}
