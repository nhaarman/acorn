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
import com.nhaarman.bravo.navigation.DisposableHandle
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.navigation.TransitionData
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.testing.TestContext.Companion.testWith

/**
 * A class that provides simple Scene/Container management for tests.
 *
 * This class acts as an observer to the [Scene] events provided by given
 * [navigator], and attaches and detaches the [Container] provided by given
 * [ContainerProvider] to the Scene at appropriate points.
 *
 * Users of this class should call [start], [stop] and [destroy] at appropriate
 * times. [testWith] does this for you.
 *
 * @see testWith
 */
class TestContext private constructor(
    private val navigator: Navigator,
    private val containerProvider: ContainerProvider
) : Navigator.Events {

    private var scene: Scene<out Container>? = null
    private var container: Container? = null

    /**
     * Returns the currently attached [Container], cast to [T].
     *
     * This function is here as syntax sugar: if the container instance is `null`
     * or is not of type T, you may get exceptions thrown.
     */
    fun <T : Container> container(): T = container as T

    private var disposable: DisposableHandle? = null
        set(value) {
            field?.dispose()
            field = value
        }

    fun start() {
        disposable = navigator.addNavigatorEventsListener(this)
        navigator.onStart()
    }

    override fun scene(scene: Scene<out Container>, data: TransitionData?) {
        (this.scene as? Scene<Container>)?.detach(container!!)
        this.scene = scene
        this.container = containerProvider.containerFor(scene).also { (scene as Scene<Container>).attach(it) }
    }

    /**
     * Returns `true` when the [navigator] has finished.
     */
    var finished = false
        private set

    override fun finished() {
        finished = true
    }

    fun pressBack() {
        (navigator as OnBackPressListener).onBackPressed()
    }

    fun stop() {
        navigator.onStop()
    }

    fun destroy() {
        navigator.onDestroy()
        disposable = null
    }

    companion object {

        /**
         * Initializes the [TestContext] and executes the code block provided
         * by [f].
         *
         * This function will take care of calling [TestContext.start],
         * [TestContext.stop] and [TestContext.destroy] for you.
         *
         * Example usage:
         *
         * ```
         * @Test
         * fun myTest() = testWith(context) {
         *   // Do the testing
         * }
         */
        fun testWith(context: TestContext, f: TestContext.() -> Unit) {
            context.start()
            context.f()
            context.stop()
            context.destroy()
        }

        /**
         * Creates a new [TestContext] instance.
         *
         * @param navigator The [Navigator] instance to use.
         * @param containerProvider The [ContainerProvider] that can provide
         * test [Container] instances to attach to [Scene] instances.
         */
        fun create(navigator: Navigator, containerProvider: ContainerProvider): TestContext {
            return TestContext(navigator, containerProvider)
        }
    }
}