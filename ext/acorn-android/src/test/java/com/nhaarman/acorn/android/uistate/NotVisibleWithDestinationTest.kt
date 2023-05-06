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
import com.nhaarman.acorn.android.util.TestView
import com.nhaarman.acorn.android.util.TestViewController
import com.nhaarman.acorn.android.util.TestViewControllerFactory
import com.nhaarman.expect.expect
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

internal class NotVisibleWithDestinationTest {

    val root = spy(RootViewGroup())
    val scene = spy(TestScene())
    val sceneView = TestView()
    val viewController = TestViewController(sceneView)
    val viewControllerFactory = TestViewControllerFactory()
    val destination = Destination(
        scene,
        viewControllerFactory,
        null,
    )

    val state = NotVisibleWithDestination(
        root,
        TestSceneTransitionFactory(),
        destination,
        null,
    )

    @BeforeEach
    fun setup() {
        viewControllerFactory.register(scene.key, viewController)
    }

    @Test
    fun `'uiNotVisible' makes no transition`() {
        expect(state.uiNotVisible()).toBe(state)
    }

    @Test
    fun `'withoutScene' results in NotVisible state`() {
        expect(state.withoutScene()).toBeInstanceOf<NotVisible>()
    }

    @Test
    fun `'withoutScene' does not touch root view group`() {
        // When
        state.withoutScene()

        // Then
        verifyNoInteractions(root)
    }

    @Test
    fun `'withScene' results in NotVisibleWithDestination state`() {
        expect(state.withScene(mock(), mock(), null)).toBeInstanceOf<NotVisibleWithDestination>()
    }

    @Test
    fun `'withScene' does not touch root view group`() {
        // When
        state.withScene(mock(), mock(), null)

        // Then
        verifyNoInteractions(root)
    }

    @Test
    fun `'uiVisible' results in VisibleWithDestination state`() {
        expect(state.uiVisible()).toBeInstanceOf<VisibleWithDestination>()
    }

    @Test
    fun `'uiVisible' replaces root children with new Scene view`() {
        // When
        state.uiVisible()

        // Then
        expect(root.views).toBe(listOf(sceneView))
    }

    @Test
    fun `'uiVisible' attaches container to scene`() {
        // When
        state.uiVisible()

        // Then
        verify(scene).attach(any())
    }

    @Test
    fun `'uiVisible' for state with existing view controller does not manipulate root`() {
        // Given
        val existingView = TestView()
        root.addView(existingView)

        val state = NotVisibleWithDestination(
            root,
            TestSceneTransitionFactory(),
            destination,
            TestViewController(TestView()),
        )

        // When
        state.uiVisible()

        // Then
        expect(root.views).toBe(listOf(existingView))
    }

    @Test
    fun `'uiVisible' for state with existing view controller attaches that controller`() {
        // Given
        val viewController = TestViewController(TestView())
        val state = NotVisibleWithDestination(
            root,
            TestSceneTransitionFactory(),
            destination,
            viewController,
        )

        // When
        state.uiVisible()

        // Then
        verify(scene).attach(viewController)
    }
}
