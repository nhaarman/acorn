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