package com.nhaarman.bravo.android.presentation

import android.support.test.InstrumentationRegistry
import android.widget.FrameLayout
import com.nhaarman.bravo.android.LinearLayoutContainer
import com.nhaarman.bravo.android.test.R
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import org.junit.Test

internal class LayoutResourceViewCreatorTest {

    @Test
    fun errorIsThrownWhenResultIsNoContainer() {
        /* Given */
        val viewGroup = FrameLayout(InstrumentationRegistry.getContext())
        val creator = LayoutResourceViewCreator(R.layout.linearlayout)

        /* Then */
        expectErrorWithMessage("View should implement com.nhaarman.bravo.presentation.Container") on {

            /* When */
            creator.create(viewGroup)
        }
    }

    @Test
    fun properViewIsReturned() {
        /* Given */
        val viewGroup = FrameLayout(InstrumentationRegistry.getContext())
        val creator = LayoutResourceViewCreator(R.layout.linearlayoutcontainer)

        /* When */
        val result = creator.create(viewGroup)

        /* Then */
        expect(result.view).toBeInstanceOf<LinearLayoutContainer>()
    }

    @Test
    fun viewIsTheContainer() {
        /* Given */
        val viewGroup = FrameLayout(InstrumentationRegistry.getContext())
        val creator = LayoutResourceViewCreator(R.layout.linearlayoutcontainer)

        /* When */
        val result = creator.create(viewGroup)

        /* Then */
        expect(result.container).toBeTheSameAs(result.view as Container)
    }
}