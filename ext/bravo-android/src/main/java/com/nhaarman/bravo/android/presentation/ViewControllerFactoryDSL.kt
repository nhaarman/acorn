/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.android.presentation

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.nhaarman.bravo.android.presentation.internal.BindingViewControllerFactory
import com.nhaarman.bravo.android.presentation.internal.InflatingViewControllerFactory
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.presentation.SceneKey

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