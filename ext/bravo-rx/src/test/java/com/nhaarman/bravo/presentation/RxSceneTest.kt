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
