/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nhaarman.acorn.android.presentation

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.nhaarman.acorn.android.presentation.internal.BindingViewControllerFactory
import com.nhaarman.acorn.android.presentation.internal.InflatingViewControllerFactory
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey

/**
 * An entry point for the [ViewControllerFactory] DSL.
 *
 * @see [ViewControllerFactoryBuilder].
 */
fun bindViews(init: ViewControllerFactoryBuilder.() -> Unit): ViewControllerFactory {
    return ViewControllerFactoryBuilder().apply(init).build()
}

/**
 * A DSL that can create [ViewControllerFactory] instances by binding [Scene]
 * keys to inflatable layouts.
 */
class ViewControllerFactoryBuilder internal constructor() {

    private val bindings = mutableMapOf<SceneKey, ViewControllerFactory>()

    /**
     * Binds [Scene]s with given [sceneKey] to the layout with given
     * [layoutResId].
     *
     * @param sceneKey The key of the scene, as provided by [Scene.key].
     * @param layoutResId The layout resource identifier to inflate for the
     * [Scene].
     * @param wrapper A function that takes in the inflated layout and returns
     * a [ViewController] instance that can be passed to the Scene.
     */
    fun bindView(
        sceneKey: SceneKey,
        @LayoutRes layoutResId: Int,
        wrapper: (View) -> ViewController
    ) {
        bindings[sceneKey] = InflatingViewControllerFactory(layoutResId, wrapper)
    }

    /**
     * Binds [Scene]s with given [sceneKey] to the layout with given
     * [layoutResId].
     *
     * @param sceneKey The key of the scene, as provided by [Scene.key].
     * @param layoutResId The layout resource identifier to inflate for the
     * [Scene].
     * @param wrapper A function that takes in the inflated layout and returns
     * a [ViewController] instance that can be passed to the Scene.
     */
    fun bindViewGroup(
        sceneKey: SceneKey,
        @LayoutRes layoutResId: Int,
        wrapper: (ViewGroup) -> ViewController
    ) {
        bindings[sceneKey] = InflatingViewControllerFactory(layoutResId, wrapper)
    }

    /**
     * Binds [Scene]s with given [sceneKey] to the layout with given
     * [layoutResId].
     *
     * @param sceneKey The key of the scene, as provided by [Scene.key].
     * @param layoutResId The layout resource identifier to inflate for the
     * [Scene].
     * @param wrapper A function that takes in the inflated layout and returns
     * a [ViewController] instance that can be passed to the Scene.
     * @param V The specialized [View] type to pass to the [wrapper] function.
     * The inflated view will be cast to this type.
     */
    fun <V : View> bind(
        sceneKey: SceneKey,
        @LayoutRes layoutResId: Int,
        wrapper: (V) -> ViewController
    ) {
        bindings[sceneKey] = InflatingViewControllerFactory(layoutResId, wrapper)
    }

    /** Constructs the [ViewControllerFactory] instance. */
    internal fun build(): ViewControllerFactory {
        return BindingViewControllerFactory(bindings)
    }
}
