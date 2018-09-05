package com.nhaarman.bravo.android.presentation

import android.support.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup
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

    private val bindings = mutableMapOf<SceneKey, ViewCreator>()

    /**
     * Binds [Scene]s with given [sceneKey] to the layout with given
     * [layoutResId].
     *
     * @param sceneKey The key of the [Scene], as provided by [Scene.key].
     * @param layoutResId The layout resource identifier to inflate for the
     * [Scene]. The root view of the inflated layout must implement the
     * [Container] contract for the [Scene].
     */
    fun bind(sceneKey: SceneKey, @LayoutRes layoutResId: Int) {
        bindings[sceneKey] = LayoutResourceViewCreator(layoutResId)
    }

    /**
     * Binds [Scene]s with given [sceneKey] to the layout with given
     * [layoutResId]. The resulting [View] is wrapped into a [Container] that
     * can be attached to the [Scene] using [wrapper].
     *
     * @param sceneKey The key of the [Scene], as provided by [Scene.key].
     * @param layoutResId The layout resource identifier to inflate for the
     * [Scene].
     * @param wrapper A function that takes in the inflated layout and returns
     * a [Container] instance that can be passed to the [Scene].
     */
    fun bind(sceneKey: SceneKey, @LayoutRes layoutResId: Int, wrapper: (View) -> Container) {
        bindings[sceneKey] = WrappedLayoutResourceViewCreator(layoutResId, wrapper)
    }

    /**
     * Binds [Scene]s with given [sceneKey] to the layout with given
     * [layoutResId]. The resulting [ViewGroup] is wrapped into a [Container]
     * that can be attached to the [Scene] using [wrapper].
     *
     * @param sceneKey The key of the [Scene], as provided by [Scene.key].
     * @param layoutResId The layout resource identifier to inflate for the
     * [Scene].
     * @param wrapper A function that takes in the inflated layout as a
     * [ViewGroup] and returns a [Container] instance that can be passed to the
     * [Scene].
     */
    fun bindViewGroup(sceneKey: SceneKey, @LayoutRes layoutResId: Int, wrapper: (ViewGroup) -> Container) {
        bindings[sceneKey] = WrappedLayoutResourceViewCreator(layoutResId, wrapper)
    }

    /** Constructs the [ViewFactory] instance. */
    internal fun build(): ViewFactory {
        return BindingViewFactory(bindings)
    }
}