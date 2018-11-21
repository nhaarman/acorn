/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.acorn.navigation

import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.mockitokotlin2.mock

class TestNavigator : Navigator {

    private var isDestroyed = false

    private var listeners = listOf<Navigator.Events>()

    fun onScene(scene: Scene<*>, data: TransitionData? = null) {
        listeners.forEach { it.scene(scene, data) }
    }

    override fun addNavigatorEventsListener(listener: Navigator.Events): DisposableHandle {
        listeners += listener
        return mock()
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
        isDestroyed = true
    }

    override fun isDestroyed(): Boolean {
        return isDestroyed
    }
}