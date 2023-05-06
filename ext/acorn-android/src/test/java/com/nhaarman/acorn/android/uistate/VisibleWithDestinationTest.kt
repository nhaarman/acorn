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
import com.nhaarman.acorn.android.util.TestSceneTransitionFactory
import com.nhaarman.acorn.android.util.TestTransition
import com.nhaarman.acorn.android.util.TestView
import com.nhaarman.acorn.android.util.TestViewController
import com.nhaarman.acorn.android.util.TestViewControllerFactory
import com.nhaarman.expect.expect
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

internal class VisibleWithDestinationTest {

    val root = spy(RootViewGroup())

    val transitionTo2 = TestTransition()
    val transition2To3 = TestTransition()
    val transitionFactory = TestSceneTransitionFactory()

    val scene = spy(TestScene())
    val sceneView = TestView()
    val sceneViewController = spy(TestViewController(sceneView))
    val uiDestination = Destination(
        scene,
        TestViewControllerFactory(),
        null,
    )

    val scene2 = spy(TestScene())
    val sceneView2 = TestView()
    val sceneViewController2 = spy(TestViewController(sceneView2))
    val sceneViewControllerFactory2 = TestViewControllerFactory()

    val scene3 = spy(TestScene())
    val sceneView3 = TestView()
    val sceneViewController3 = spy(TestViewController(sceneView3))
    val sceneViewControllerFactory3 = TestViewControllerFactory()

    val state = VisibleWithDestination(
        root,
        transitionFactory,
        uiDestination,
        sceneViewController,
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
        fun `'onBackPressed' delegates to current ViewController`() {
            // Given
            whenever(sceneViewController.onBackPressed()).thenReturn(true)

            // When
            val result = state.onBackPressed()

            // Then
            verify(sceneViewController).onBackPressed()
            expect(result).toBe(true)
        }

        @Test
        fun `'uiNotVisible' results in NotVisibleWithDestination state`() {
            expect(state.uiNotVisible()).toBeInstanceOf<NotVisibleWithDestination>()
        }

        @Test
        fun `'uiNotVisible' detaches view controller`() {
            // When
            state.uiNotVisible()

            // Then
            verify(scene).detach(sceneViewController)
        }

        @Test
        fun `'withoutScene' results in Visible state`() {
            expect(state.withoutScene()).toBeInstanceOf<Visible>()
        }

        @Test
        fun `'withoutScene' detaches current view controller`() {
            // When
            state.withoutScene()

            // Then
            verify(scene).detach(sceneViewController)
        }

        @Test
        fun `'withScene' detaches current view controller`() {
            // When
            state.withScene(scene2, sceneViewControllerFactory2, null)

            // Then
            verify(scene).detach(sceneViewController)
        }

        @Test
        fun `'withScene' executes transition`() {
            // When
            state.withScene(scene2, sceneViewControllerFactory2, null)

            // Then
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
        fun `'onBackPressed' does not delegate`() {
            // Given
            whenever(sceneViewController.onBackPressed()).thenReturn(true)

            // When
            val result = state.onBackPressed()

            // Then
            verify(sceneViewController, never()).onBackPressed()
            expect(result).toBe(false)
        }

        @Test
        fun `transition requesting 'attach' actually attaches`() {
            // When
            transitionTo2.attach(sceneViewController2)

            // Then
            verify(scene2).attach(sceneViewController2)
        }

        @Test
        fun `transition requesting 'attach' multiple times only attaches once`() {
            // When
            transitionTo2.attach(sceneViewController2)
            transitionTo2.attach(sceneViewController2)
            transitionTo2.attach(sceneViewController2)

            // Then
            verify(scene2, times(1)).attach(sceneViewController2)
        }

        @Test
        fun `transition requesting 'onComplete' without 'attach' attaches`() {
            // When
            transitionTo2.complete(sceneViewController2)

            // Then
            verify(scene2).attach(sceneViewController2)
        }

        @Test
        fun `transition requesting 'onComplete' twice without 'attach' only attaches once`() {
            // When
            transitionTo2.complete(sceneViewController2)
            transitionTo2.complete(sceneViewController2)

            // Then
            verify(scene2, times(1)).attach(sceneViewController2)
        }

        @Test
        fun `transition requesting 'onComplete' after 'attach' only attaches once`() {
            // When
            transitionTo2.attach(sceneViewController2)
            transitionTo2.complete(sceneViewController2)

            // Then
            verify(scene2, times(1)).attach(sceneViewController2)
        }

        @Test
        fun `transition requesting 'attach' after 'onComplete' only attaches once`() {
            // When
            transitionTo2.complete(sceneViewController2)
            transitionTo2.attach(sceneViewController2)

            // Then
            verify(scene2, times(1)).attach(sceneViewController2)
        }
    }

    @Nested
    inner class TransitionInProgress {

        @BeforeEach
        fun setup() {
            sceneViewControllerFactory2.register(scene2.key, sceneViewController2)
            sceneViewControllerFactory3.register(scene3.key, sceneViewController3)

            state.withScene(scene2, sceneViewControllerFactory2, null)
        }

        @Test
        fun `'onBackPressed' does not delegate`() {
            // Given
            whenever(sceneViewController.onBackPressed()).thenReturn(true)

            // When
            val result = state.onBackPressed()

            // Then
            verify(sceneViewController, never()).onBackPressed()
            expect(result).toBe(false)
        }

        @Test
        fun `transition requesting 'attach' after 'uiNotVisible' does not attach`() {
            // Given
            state.uiNotVisible()

            // When
            transitionTo2.attach(sceneViewController2)

            // Then
            verify(scene2, never()).attach(sceneViewController2)
        }

        @Test
        fun `transition requesting 'onComplete' after 'uiNotVisible' does not attach`() {
            // Given
            state.uiNotVisible()

            // When
            transitionTo2.complete(sceneViewController2)

            // Then
            verify(scene2, never()).attach(sceneViewController2)
        }

        @Test
        fun `transition requesting 'attach' after 'withoutScene' does not attach`() {
            // Given
            state.withoutScene()

            // When
            transitionTo2.attach(sceneViewController2)

            // Then
            verify(scene2, never()).attach(sceneViewController2)
        }

        @Test
        fun `transition requesting 'onComplete' after 'withoutScene' does not attach`() {
            // Given
            state.withoutScene()

            // When
            transitionTo2.complete(sceneViewController2)

            // Then
            verify(scene2, never()).attach(sceneViewController2)
        }

        @Test
        fun `'withScene' schedules transition`() {
            // When
            state.withScene(scene3, sceneViewControllerFactory3, null)

            // Then
            expect(transition2To3.isStarted()).notToHold()
        }

        @Test
        fun `first transition completed executes scheduled transition`() {
            // Given
            state.withScene(scene3, sceneViewControllerFactory3, null)

            // When
            transitionTo2.complete(sceneViewController2)

            // Then
            expect(transition2To3.isStarted()).toHold()
        }

        @Test
        fun `'withoutScene' does not detach current view controller for a second time`() {
            // When
            state.withoutScene()

            // Then
            verify(scene, times(1)).detach(sceneViewController)
        }

        @Test
        fun `'uiNotVisible' during a transition remembers the transition's destination`() {
            // When
            val newState = state.uiNotVisible()

            // Then
            verify(scene2, never()).attach(any())

            // When
            newState.uiVisible()

            // Then
            verify(scene2).attach(sceneViewController2)
        }

        @Test
        fun `'uiNotVisible' during a transition with a scheduled remembers the scheduled transition's destination`() {
            // Given
            state.withScene(scene3, sceneViewControllerFactory3, null)

            // When
            val newState = state.uiNotVisible()

            // Then
            verify(scene3, never()).attach(any())

            // When
            newState.uiVisible()

            // Then
            verify(scene3).attach(sceneViewController3)
        }
    }

    @Nested
    inner class TransitionFinished {

        @BeforeEach
        fun setup() {
            state.withScene(scene2, sceneViewControllerFactory2, null)
            transitionTo2.complete(sceneViewController2)
        }

        @Test
        fun `'onBackPressed' delegates to new ViewController`() {
            // Given
            whenever(sceneViewController.onBackPressed()).thenReturn(false)
            whenever(sceneViewController2.onBackPressed()).thenReturn(true)

            // When
            val result = state.onBackPressed()

            // Then
            verify(sceneViewController, never()).onBackPressed()
            verify(sceneViewController2).onBackPressed()
            expect(result).toBe(true)
        }
    }
}
