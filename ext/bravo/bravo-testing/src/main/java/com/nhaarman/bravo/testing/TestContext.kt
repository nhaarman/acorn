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

@file:Suppress("UNCHECKED_CAST")

package com.nhaarman.bravo.testing

import com.nhaarman.bravo.OnBackPressListener
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.navigation.TransitionData
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

/**
 * A class that provides simple Scene/Container management for tests.
 */
class TestContext private constructor(
    private val navigator: Navigator<Navigator.Events>,
    private val containerProvider: ContainerProvider
) : Navigator.Events {

    private var scene: Scene<out Container>? = null
    private var container: Container? = null
    fun <T> container(): T = container as T

    init {
        navigator.addListener(this)
        navigator.onStart()
    }

    override fun scene(scene: Scene<out Container>, data: TransitionData?) {
        (this.scene as? Scene<Container>)?.detach(container!!)
        this.scene = scene
        this.container = containerProvider.containerFor(scene).also { (scene as Scene<Container>).attach(it) }
    }

    var finished = false
    override fun finished() {
        finished = true
    }

    fun onBackPressed() {
        (navigator as OnBackPressListener).onBackPressed()
    }

    companion object {

        fun create(navigator: Navigator<*>, containerProvider: ContainerProvider): TestContext {
            return TestContext(navigator as Navigator<Navigator.Events>, containerProvider)
        }
    }
}