package com.nhaarman.bravo.android.presentation

import android.support.test.InstrumentationRegistry
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.nhaarman.bravo.android.test.R
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.expect.expect
import org.junit.Test

internal class WrappedLayoutResourceViewCreatorTest {

    @Test
    fun properViewIsReturned() {
        /* Given */
        val viewGroup = FrameLayout(InstrumentationRegistry.getContext())
        val creator = WrappedLayoutResourceViewCreator<View>(R.layout.linearlayout) {
            MyContainer(it)
        }

        /* When */
        val result = creator.create(viewGroup)

        /* Then */
        expect(result.view).toBeInstanceOf<LinearLayout>()
    }

    @Test
    fun properContainerIsReturned() {
        /* Given */
        val viewGroup = FrameLayout(InstrumentationRegistry.getContext())
        val creator = WrappedLayoutResourceViewCreator<View>(R.layout.linearlayout) {
            MyContainer(it)
        }

        /* When */
        val result = creator.create(viewGroup)

        /* Then */
        expect(result.container).toBeInstanceOf<MyContainer> {
            expect(it.v).toBe(result.view)
        }
    }

    private class MyContainer(val v: View) : Container
}