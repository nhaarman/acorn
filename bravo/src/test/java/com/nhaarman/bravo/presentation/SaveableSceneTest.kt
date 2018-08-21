package com.nhaarman.bravo.presentation

import com.nhaarman.bravo.BravoBundle
import com.nhaarman.bravo.StateRestorable
import com.nhaarman.expect.expect
import org.junit.jupiter.api.Test

class SaveableSceneTest {

    private val view1 = TestView()
    private val view2 = TestView()

    @Test
    fun `view state is restored when new view attaches`() {
        /* Given */
        val scene = TestSaveableScene(null)
        scene.attach(view1)
        view1.state = 3

        /* When */
        scene.detach(view1)
        scene.attach(view2)

        /* Then */
        expect(view2.state).toBe(3)
    }

    @Test
    fun `view state is restored from scene state`() {
        /* Given */
        val scene1 = TestSaveableScene(null)

        scene1.attach(view1)
        view1.state = 3

        /* When */
        scene1.detach(view1)
        val state = scene1.saveInstanceState()
        val scene2 = TestSaveableScene(state)
        scene2.attach(view2)

        /* Then */
        expect(view2.state).toBe(3)
    }

    @Test
    fun `view state is restored from scene state -- without detach`() {
        /* Given */
        val scene1 = TestSaveableScene(null)

        scene1.attach(view1)
        view1.state = 3

        /* When */
        val state = scene1.saveInstanceState()
        val scene2 = TestSaveableScene(state)
        scene2.attach(view2)

        /* Then */
        expect(view2.state).toBe(3)
    }

    private class TestSaveableScene(viewState: BravoBundle?) : SaveableScene<TestView>(viewState)
    private class TestView : Container, StateRestorable {

        var state: Int? = null

        override fun saveInstanceState(): BravoBundle {
            return BravoBundle.bundle {
                it["state"] = state
            }
        }

        override fun restoreInstanceState(bundle: BravoBundle) {
            state = bundle["state"]
        }
    }
}
