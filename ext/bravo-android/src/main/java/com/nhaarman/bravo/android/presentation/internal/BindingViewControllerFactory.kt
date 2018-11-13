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

package com.nhaarman.bravo.android.presentation.internal

import android.view.ViewGroup
import com.nhaarman.bravo.android.presentation.ViewController
import com.nhaarman.bravo.android.presentation.ViewControllerFactory
import com.nhaarman.bravo.presentation.SceneKey

/**
 * A [ViewControllerFactory] implementation that binds [SceneKey]s to
 * [ViewControllerFactory] instances to create views.
 */
internal class BindingViewControllerFactory(
    private val bindings: Map<SceneKey, ViewControllerFactory>
) : ViewControllerFactory {

    override fun supports(sceneKey: SceneKey): Boolean {
        return bindings.containsKey(sceneKey)
    }

    override fun viewControllerFor(sceneKey: SceneKey, parent: ViewGroup): ViewController {
        val viewControllerFactory = bindings[sceneKey]
            ?: throw IllegalStateException("Could not create ViewController for Scene with key $sceneKey.")

        return viewControllerFactory.viewControllerFor(sceneKey, parent)
    }
}
