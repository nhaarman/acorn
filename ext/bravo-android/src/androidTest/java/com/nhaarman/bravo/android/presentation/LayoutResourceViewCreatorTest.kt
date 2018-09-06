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