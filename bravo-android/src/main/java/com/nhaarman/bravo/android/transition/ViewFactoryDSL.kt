package com.nhaarman.bravo.android.transition

import android.support.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup
import com.nhaarman.bravo.android.presentation.ViewFactory
import com.nhaarman.bravo.android.presentation.ViewResult
import com.nhaarman.bravo.android.transition.Binding.ViewResourceBinding
import com.nhaarman.bravo.android.transition.Binding.WrappedViewGroupResourceBinding
import com.nhaarman.bravo.android.transition.Binding.WrappedViewResourceBinding
import com.nhaarman.bravo.android.util.inflate
import com.nhaarman.bravo.android.util.inflateView
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.presentation.SceneKey

/**
 * An entry point for the [ViewFactory] DSL.
 *
 * @see [ViewFactoryBuilder].
 */
fun bindViews(init: ViewFactoryBuilder.() -> Unit): ViewFactory {
    return ViewFactoryBuilder().apply(init).build()
}

/**
 * A DSL that can create [ViewFactory] instances by binding [Scene] keys to
 * inflatable layouts.
 */
class ViewFactoryBuilder internal constructor() {

    private val bindings = mutableMapOf<SceneKey, Binding>()

    /**
     * Binds [Scene]s with given [sceneKey] to the layout with given
     * [layoutResId].
     *
     * @param sceneKey The key of the [Scene], as provided by [Scene.key].
     * @param layoutResId The layout resource identifier to inflate for the
     *                    [Scene]. The root view of the inflated layout must
     *                    implement the [Container] contract for the [Scene].
     */
    fun bind(sceneKey: SceneKey, @LayoutRes layoutResId: Int) {
        bindings[sceneKey] = ViewResourceBinding(layoutResId)
    }

    /**
     * Binds [Scene]s with given [sceneKey] to the layout with given
     * [layoutResId]. The resulting [View] is wrapped into a [Container] that
     * can be attached to the [Scene] using [wrapper].
     *
     * @param sceneKey The key of the [Scene], as provided by [Scene.key].
     * @param layoutResId The layout resource identifier to inflate for the
     *                    [Scene].
     * @param wrapper A function that takes in the inflated layout and returns
     *                a [Container] instance that can be passed to the [Scene].
     */
    fun bind(sceneKey: SceneKey, @LayoutRes layoutResId: Int, wrapper: (View) -> Container) {
        bindings[sceneKey] = WrappedViewResourceBinding(layoutResId, wrapper)
    }

    /**
     * Binds [Scene]s with given [sceneKey] to the layout with given
     * [layoutResId]. The resulting [ViewGroup] is wrapped into a [Container]
     * that can be attached to the [Scene] using [wrapper].
     *
     * @param sceneKey The key of the [Scene], as provided by [Scene.key].
     * @param layoutResId The layout resource identifier to inflate for the
     *                    [Scene].
     * @param wrapper A function that takes in the inflated layout as a
     *                [ViewGroup] and returns a [Container] instance that can be
     *                passed to the [Scene].
     */
    fun bindViewGroup(sceneKey: SceneKey, @LayoutRes layoutResId: Int, wrapper: (ViewGroup) -> Container) {
        bindings[sceneKey] = WrappedViewGroupResourceBinding(layoutResId, wrapper)
    }

    /** Constructs the [ViewFactory] instance. */
    internal fun build(): ViewFactory {
        return DefaultViewFactory(bindings)
    }
}

internal class DefaultViewFactory(
    private val bindings: Map<SceneKey, Binding>
) : ViewFactory {

    override fun viewFor(sceneKey: SceneKey, parent: ViewGroup): ViewResult {
        return bindings[sceneKey]
            ?.create(parent)
            ?: error("Unable to create view for Scene with key: \"$sceneKey\".")
    }
}

internal sealed class Binding {

    abstract fun create(parent: ViewGroup): ViewResult

    class ViewResourceBinding(
        @LayoutRes private val layoutResId: Int
    ) : Binding() {

        override fun create(parent: ViewGroup): ViewResult {
            return parent
                .inflateView(layoutResId)
                .let { ViewResult.from(it) }
        }
    }

    class WrappedViewResourceBinding(
        @LayoutRes private val layoutResId: Int,
        private val wrapper: (View) -> Container
    ) : Binding() {

        override fun create(parent: ViewGroup): ViewResult {
            return parent
                .inflateView(layoutResId)
                .let { view -> ViewResult.from(view, wrapper.invoke(view)) }
        }
    }

    class WrappedViewGroupResourceBinding(
        @LayoutRes private val layoutResId: Int,
        private val wrapper: (ViewGroup) -> Container
    ) : Binding() {
        override fun create(parent: ViewGroup): ViewResult {
            return parent
                .inflate<ViewGroup>(layoutResId)
                .let { viewGroup -> ViewResult.from(viewGroup, wrapper.invoke(viewGroup)) }
        }
    }
}
