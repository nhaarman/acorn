package com.nhaarman.bravo.android.transition

import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.presentation.SceneKey

interface TransitionFactory {

    fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>): Transition
}

class SimpleTransitionFactory(
    private val viewFactory: ViewFactory,
    private val transitions: Map<Pair<SceneKey, SceneKey>, Transition>,
    private val classTransitions: Map<Pair<Class<out Scene<*>>, Class<out Scene<*>>>, Transition>,
    private val classTransitions2: MutableMap<Pair<Class<out Scene<*>>, Class<out Scene<*>>>, (Scene<*>) -> Transition>
) : TransitionFactory {

    override fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>): Transition {
        return transitions[previousScene.key to newScene.key]
            ?: classTransitions[previousScene::class.java to newScene::class.java]
            ?: classTransitions2[previousScene::class.java to newScene::class.java]?.invoke(newScene)
            ?: FadeInFromBottomTransition { parent ->
                viewFactory.viewFor(
                    newScene.key,
                    parent
                )
            }
    }
}

class DefaultTransitionFactory(
    private val viewFactory: ViewFactory
) : TransitionFactory {

    override fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>): Transition {
        return FadeInFromBottomTransition { parent ->
            viewFactory.viewFor(
                newScene.key,
                parent
            )
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
