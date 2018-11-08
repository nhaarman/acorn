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

package com.nhaarman.bravo.android.uistate

import com.nhaarman.bravo.android.uistate.internal.Destination
import com.nhaarman.bravo.android.util.RootViewGroup
import com.nhaarman.bravo.android.util.TestScene
import com.nhaarman.bravo.android.util.TestTransition
import com.nhaarman.bravo.android.util.TestTransitionFactory
import com.nhaarman.bravo.android.util.TestView
import com.nhaarman.bravo.android.util.TestViewController
import com.nhaarman.bravo.android.util.TestViewControllerProvider
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class VisibleWithDestinationTest {

    val root = spy(RootViewGroup())

    val transitionTo2 = TestTransition()
    val transition2To3 = TestTransition()
    val transitionFactory = TestTransitionFactory()

    val scene = spy(TestScene())
    val sceneView = TestView()
    val sceneViewController = TestViewController(sceneView)
    val uiDestination = Destination(
        scene,
        TestViewControllerProvider(sceneViewController),
        null
    )

    val scene2 = spy(TestScene())
    val sceneView2 = TestView()
    val sceneViewController2 = TestViewController(sceneView2)
    val sceneViewControllerProvider2 = TestViewControllerProvider(sceneViewController2)

    val scene3 = spy(TestScene())
    val sceneViewControllerProvider3 = TestViewControllerProvider(TestViewController(TestView()))

    val state = VisibleWithDestination(
        root,
        transitionFactory,
        uiDestination,
        sceneViewController
    )

    @BeforeEach
    fun setup() {
        transitionFactory.transitions += (scene to scene2) to transitionTo2
        transitionFactory.transitions += (scene2 to scene3) to transition2To3

        root.addView(sceneView)
        scene.attach(sceneViewController)
    }

    @Test
    fun `'uiVisible' makes no transition`() {
        expect(state.uiVisible()).toBe(state)
    }

    @Nested
    inner class NoTransitionInProgress {

        @Test
        fun `'uiNotVisible' results in NotVisibleWithDestination state`() {
            expect(state.uiNotVisible()).toBeInstanceOf<NotVisibleWithDestination>()
        }

        @Test
        fun `'uiNotVisible' detaches view controller`() {
            /* When */
            state.uiNotVisible()

            /* Then */
            verify(scene).detach(sceneViewController)
        }

        @Test
        fun `'withoutScene' results in Visible state`() {
            expect(state.withoutScene()).toBeInstanceOf<Visible>()
        }

        @Test
        fun `'withoutScene' detaches current view controller`() {
            /* When */
            state.withoutScene()

            /* Then */
            verify(scene).detach(sceneViewController)
        }

        @Test
        fun `'withScene' detaches current view controller`() {
            /* When */
            state.withScene(scene2, sceneViewControllerProvider2, null)

            /* Then */
            verify(scene).detach(sceneViewController)
        }

        @Test
        fun `'withScene' executes transition`() {
            /* When */
            state.withScene(scene2, sceneViewControllerProvider2, null)

            /* Then */
            expect(transitionTo2.isStarted()).toHold()
        }
    }

    @Nested
    inner class Transitioning {

        @BeforeEach
        fun setup() {
            state.withScene(scene2, sceneViewControllerProvider2, null)
        }

        @Test
        fun `transition requesting 'attach' actually attaches`() {
            /* When */
            transitionTo2.attach(sceneViewController2)

            /* Then */
            verify(scene2).attach(sceneViewController2)
        }

        @Test
        fun `transition requesting 'attach' multiple times only attaches once`() {
            /* When */
            transitionTo2.attach(sceneViewController2)
            transitionTo2.attach(sceneViewController2)
            transitionTo2.attach(sceneViewController2)

            /* Then */
            verify(scene2, times(1)).attach(sceneViewController2)
        }

        @Test
        fun `transition requesting 'onComplete' without 'attach' attaches`() {
            /* When */
            transitionTo2.complete(sceneViewController2)

            /* Then */
            verify(scene2).attach(sceneViewController2)
        }

        @Test
        fun `transition requesting 'onComplete' twice without 'attach' only attaches once`() {
            /* When */
            transitionTo2.complete(sceneViewController2)
            transitionTo2.complete(sceneViewController2)

            /* Then */
            verify(scene2, times(1)).attach(sceneViewController2)
        }

        @Test
        fun `transition requesting 'onComplete' after 'attach' only attaches once`() {
            /* When */
            transitionTo2.attach(sceneViewController2)
            transitionTo2.complete(sceneViewController2)

            /* Then */
            verify(scene2, times(1)).attach(sceneViewController2)
        }

        @Test
        fun `transition requesting 'attach' after 'onComplete' only attaches once`() {
            /* When */
            transitionTo2.complete(sceneViewController2)
            transitionTo2.attach(sceneViewController2)

            /* Then */
            verify(scene2, times(1)).attach(sceneViewController2)
        }
    }

    @Nested
    inner class TransitionInProgress {

        @BeforeEach
        fun setup() {
            state.withScene(scene2, sceneViewControllerProvider2, null)
        }

        @Test
        fun `transition requesting 'attach' after 'uiNotVisible' does not attach`() {
            /* Given */
            state.uiNotVisible()

            /* When */
            transitionTo2.attach(sceneViewController2)

            /* Then */
            verify(scene2, never()).attach(sceneViewController2)
        }

        @Test
        fun `transition requesting 'onComplete' after 'uiNotVisible' does not attach`() {
            /* Given */
            state.uiNotVisible()

            /* When */
            transitionTo2.complete(sceneViewController2)

            /* Then */
            verify(scene2, never()).attach(sceneViewController2)
        }

        @Test
        fun `transition requesting 'attach' after 'withoutScene' does not attach`() {
            /* Given */
            state.withoutScene()

            /* When */
            transitionTo2.attach(sceneViewController2)

            /* Then */
            verify(scene2, never()).attach(sceneViewController2)
        }

        @Test
        fun `transition requesting 'onComplete' after 'withoutScene' does not attach`() {
            /* Given */
            state.withoutScene()

            /* When */
            transitionTo2.complete(sceneViewController2)

            /* Then */
            verify(scene2, never()).attach(sceneViewController2)
        }

        @Test
        fun `'withScene' schedules transition`() {
            /* When */
            state.withScene(scene3, sceneViewControllerProvider3, null)

            /* Then */
            expect(transition2To3.isStarted()).notToHold()
        }

        @Test
        fun `first transition completed executes scheduled transition`() {
            /* Given */
            state.withScene(scene3, sceneViewControllerProvider3, null)

            /* When */
            transitionTo2.complete(sceneViewController2)

            /* Then */
            expect(transition2To3.isStarted()).toHold()
        }

        @Test
        fun `'withoutScene' does not detach current view controller for a second time`() {
            /* When */
            state.withoutScene()

            /* Then */
            verify(scene, times(1)).detach(sceneViewController)
        }
    }
}
