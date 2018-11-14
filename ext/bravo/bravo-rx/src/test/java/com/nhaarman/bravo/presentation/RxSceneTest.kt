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

package com.nhaarman.bravo.presentation

import arrow.core.Option
import com.nhaarman.bravo.state.ContainerState
import com.nhaarman.bravo.state.SceneState
import com.nhaarman.bravo.state.containerState
import com.nhaarman.bravo.state.get
import com.nhaarman.expect.expect
import com.nhaarman.expect.lastValue
import org.junit.jupiter.api.Test

class RxSceneTest {

    private val scene = TestRxScene()
    private val testView = TestView()

    @Test
    fun `initially there is no view`() {
        /* When */
        val observer = scene.viewObservable.test()

        /* Then */
        expect(observer.lastValue).toBe(Option.empty())
    }

    @Test
    fun `attaching a view notifies observers`() {
        /* Given */
        val observer = scene.viewObservable.test()

        /* When */
        scene.attach(testView)

        /* Then */
        expect(observer.lastValue).toBe(Option.just(testView))
    }

    @Test
    fun `detaching a view notifies observers`() {
        /* Given */
        val observer = scene.viewObservable.test()

        /* When */
        scene.attach(testView)
        scene.detach(testView)

        /* Then */
        expect(observer.lastValue).toBe(Option.empty())
    }

    @Test
    fun `view state is restored between views`() {
        /* Given */
        val view1 = TestView(1)
        val view2 = TestView(2)

        /* When */
        scene.attach(view1)
        view1.state = 3
        scene.detach(view1)
        scene.attach(view2)

        /* Then */
        expect(view2.state).toBe(3)
    }

    private class TestRxScene(savedState: SceneState? = null) : RxScene<TestView>(savedState) {

        val viewObservable get() = view
    }

    private class TestView(var state: Int? = null) : Container, RestorableContainer {

        override fun saveInstanceState(): ContainerState {
            return containerState {
                it["state"] = state
            }
        }

        override fun restoreInstanceState(bundle: ContainerState) {
            state = bundle["state"]
        }
    }
}
