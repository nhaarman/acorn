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

package com.nhaarman.acorn.android.tests

import androidx.test.rule.ActivityTestRule
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey

class AcornViewTestRule<C : Container>(
    private val viewControllerFactory: ViewControllerFactory,
    private val sceneKey: SceneKey
) : ActivityTestRule<AcornTestActivity>(AcornTestActivity::class.java) {

    private val viewController by lazy {
        viewControllerFactory.viewControllerFor(TestScene(sceneKey), activity.findViewById(android.R.id.content))
    }

    @Suppress("UNCHECKED_CAST")
    val container: C
        get() = viewController as C

    override fun afterActivityLaunched() {
        runOnUiThread { activity.setContentView(viewController.view) }
    }

    fun onUiThread(f: AcornViewTestRule<C>.() -> Unit) {
        runOnUiThread { f(this) }
    }

    private inner class TestScene(override val key: SceneKey) : Scene<C>
}