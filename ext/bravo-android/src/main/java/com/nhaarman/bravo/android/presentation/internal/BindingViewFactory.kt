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
import com.nhaarman.bravo.android.presentation.ViewFactory
import com.nhaarman.bravo.presentation.SceneKey

/**
 * A [ViewFactory] implementation that binds [SceneKey]s to [ViewCreator]
 * instances to create views.
 */
internal class BindingViewFactory(
    private val bindings: Map<SceneKey, ViewCreator>
) : ViewFactory {

    override fun viewFor(sceneKey: SceneKey, parent: ViewGroup): ViewController? {
        return bindings[sceneKey]?.create(parent)
    }
}
