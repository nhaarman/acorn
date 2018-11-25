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

package com.nhaarman.acorn.android.util

import android.view.ViewGroup
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey

class TestViewControllerFactory : ViewControllerFactory {

    private var controllers = mapOf<SceneKey, ViewController>()

    fun register(sceneKey: SceneKey, viewController: ViewController) {
        controllers += sceneKey to viewController
    }

    override fun supports(scene: Scene<*>): Boolean {
        return controllers.containsKey(scene.key)
    }

    override fun viewControllerFor(scene: Scene<*>, parent: ViewGroup): ViewController {
        return controllers[scene.key]!!
    }
}