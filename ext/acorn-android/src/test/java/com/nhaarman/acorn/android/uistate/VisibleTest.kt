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

import com.nhaarman.acorn.android.util.RootViewGroup
import com.nhaarman.acorn.android.util.TestScene
import com.nhaarman.acorn.android.util.TestSceneTransitionFactory
import com.nhaarman.acorn.android.util.TestView
import com.nhaarman.acorn.android.util.TestViewController
import com.nhaarman.acorn.android.util.TestViewControllerFactory
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class VisibleTest {

    val root = spy(RootViewGroup())
    val scene = spy(TestScene())
    val sceneView = TestView()
    val viewController = TestViewController(sceneView)
    val viewControllerFactory = TestViewControllerFactory()

    val state = Visible(
        root,
        TestSceneTransitionFactory()
    )

    @BeforeEach
    fun setup() {
        viewControllerFactory.register(scene.key, viewController)
    }

    @Test
    fun `'uiVisible' makes no transition`() {
        expect(state.uiVisible()).toBe(state)
    }

    @Test
    fun `'withoutScene' makes no transition`() {
        expect(state.withoutScene()).toBe(state)
    }

    @Test
    fun `'uiNotVisible' results in NotVisible state`() {
        expect(state.uiNotVisible()).toBeInstanceOf<NotVisible>()
    }

    @Test
    fun `'withScene' results in VisibleWithDestination state`() {
        expect(state.withScene(scene, viewControllerFactory, null)).toBeInstanceOf<VisibleWithDestination>()
    }

    @Test
    fun `'withScene' replaces root children with new Scene view`() {
        /* When */
        state.withScene(scene, viewControllerFactory, null)

        /* Then */
        expect(root.views).toBe(listOf(sceneView))
    }

    @Test
    fun `'withScene' attaches container to scene`() {
        /* When */
        state.withScene(scene, viewControllerFactory, null)

        /* Then */
        verify(scene).attach(any())
    }
}
