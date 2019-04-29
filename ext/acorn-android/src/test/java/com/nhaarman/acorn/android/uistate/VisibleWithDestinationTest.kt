/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nhaarman.acorn.android.uistate

import com.nhaarman.acorn.android.uistate.internal.Destination
import com.nhaarman.acorn.android.util.RootViewGroup
import com.nhaarman.acorn.android.util.TestScene
import com.nhaarman.acorn.android.util.TestTransition
import com.nhaarman.acorn.android.util.TestSceneTransitionFactory
import com.nhaarman.acorn.android.util.TestView
import com.nhaarman.acorn.android.util.TestViewController
import com.nhaarman.acorn.android.util.TestViewControllerFactory
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
    val transitionFactory = TestSceneTransitionFactory()

    val scene = spy(TestScene())
    val sceneView = TestView()
    val sceneViewController = TestViewController(sceneView)
    val uiDestination = Destination(
        scene,
        TestViewControllerFactory(),
        null
    )

    val scene2 = spy(TestScene())
    val sceneView2 = TestView()
    val sceneViewController2 = TestViewController(sceneView2)
    val sceneViewControllerFactory2 = TestViewControllerFactory()

    val scene3 = spy(TestScene())
    val sceneViewControllerFactory3 = TestViewControllerFactory()

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
            state.withScene(scene2, sceneViewControllerFactory2, null)

            /* Then */
            verify(scene).detach(sceneViewController)
        }

        @Test
        fun `'withScene' executes transition`() {
            /* When */
            state.withScene(scene2, sceneViewControllerFactory2, null)

            /* Then */
            expect(transitionTo2.isStarted()).toHold()
        }
    }

    @Nested
    inner class Transitioning {

        @BeforeEach
        fun setup() {
            state.withScene(scene2, sceneViewControllerFactory2, null)
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
            state.withScene(scene2, sceneViewControllerFactory2, null)
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
            state.withScene(scene3, sceneViewControllerFactory3, null)

            /* Then */
            expect(transition2To3.isStarted()).notToHold()
        }

        @Test
        fun `first transition completed executes scheduled transition`() {
            /* Given */
            state.withScene(scene3, sceneViewControllerFactory3, null)

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
