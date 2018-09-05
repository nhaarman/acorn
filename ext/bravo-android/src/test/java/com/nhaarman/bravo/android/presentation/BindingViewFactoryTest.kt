package com.nhaarman.bravo.android.presentation

import android.view.ViewGroup
import com.nhaarman.bravo.presentation.SceneKey
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test

internal class BindingViewFactoryTest {

    @Test
    fun `empty factory`() {
        /* Given */
        val factory = BindingViewFactory(emptyMap())

        /* When */
        val result = factory.viewFor(SceneKey("test"), mock())

        /* Then */
        expect(result).toBeNull()
    }

    @Test
    fun `factory with missing key`() {
        /* Given */
        val factory = BindingViewFactory(
            mapOf(SceneKey("1") to MyViewCreator())
        )

        /* When */
        val result = factory.viewFor(SceneKey("2"), mock())

        /* Then */
        expect(result).toBeNull()
    }

    @Test
    fun `proper result`() {
        /* Given */
        val myViewCreator = MyViewCreator()
        val factory = BindingViewFactory(
            mapOf(SceneKey("1") to myViewCreator)
        )

        /* When */
        val result = factory.viewFor(SceneKey("1"), mock())

        /* Then */
        expect(result).toBe(myViewCreator.result)
    }

    class MyViewCreator : ViewCreator {

        var result: ViewResult = mock()

        override fun create(parent: ViewGroup): ViewResult {
            return result
        }
    }
}