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

package com.nhaarman.bravo.android.navigation

import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.navigation.SingleSceneNavigator
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.state.SceneState
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test

internal class AbstractNavigatorProviderTest {

    private val navigatorProvider = TestAbstractNavigatorProvider()

    @Test
    fun `navigator is cached`() {
        /* When */
        val result1 = navigatorProvider.navigatorFor(null)
        val result2 = navigatorProvider.navigatorFor(null)

        /* Then */
        expect(result1).toBeTheSameAs(result2)
    }

    @Test
    fun `destroyed navigator is renewed`() {
        /* Given */
        val result1 = navigatorProvider.navigatorFor(null)
        result1.onDestroy()

        /* When */
        val result2 = navigatorProvider.navigatorFor(null)

        /* Then */
        expect(result1).toNotBeTheSameAs(result2)
    }

    private class TestNavigator : SingleSceneNavigator<Navigator.Events>(null) {

        override fun createScene(state: SceneState?): Scene<out Container> {
            return mock()
        }
    }

    private class TestAbstractNavigatorProvider : AbstractNavigatorProvider<TestNavigator>() {

        override fun createNavigator(savedState: NavigatorState?): TestNavigator {
            return TestNavigator()
        }
    }
}