/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.acorn.notesapp.android.ui.itemlist

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.nhaarman.acorn.android.tests.AcornViewTestRule
import com.nhaarman.acorn.notesapp.android.R
import com.nhaarman.acorn.notesapp.android.ViewFactoryProvider
import com.nhaarman.acorn.notesapp.note.NoteItem
import com.nhaarman.acorn.notesapp.presentation.itemlist.ItemListContainer
import com.nhaarman.acorn.notesapp.presentation.itemlist.ItemListScene
import com.nhaarman.expect.expect
import com.nhaarman.expect.lastValue
import io.reactivex.android.schedulers.AndroidSchedulers
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep

class ItemListViewTest {

    @Rule @JvmField val rule = AcornViewTestRule<ItemListContainer>(
        ViewFactoryProvider.viewFactory,
        ItemListScene.key
    )

    @Test
    fun singleItemList() {
        /* When */
        rule.onUiThread { container.items = listOf(NoteItem(id = 3, text = "Hello!")) }

        /* Then */
        onView(withText("Hello!")).check(matches(isDisplayed()))
    }

    @Test
    fun multiItemList() {
        /* Given */
        val notes = (0..3).map { NoteItem(it.toLong(), "$it") }

        /* When */
        rule.onUiThread { container.items = notes }

        /* Then */
        notes.forEach { noteItem ->
            onView(withText(noteItem.text)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun hugeItemList() {
        /* Given */
        val notes = (0..3000).map { NoteItem(it.toLong(), "$it") }

        /* When */
        rule.onUiThread { container.items = notes }
        onView(withId(R.id.itemsRecyclerView)).perform(
            scrollToPosition<RecyclerView.ViewHolder>(3000)
        )

        /* Then */
        onView(withText("3000")).check(matches(isDisplayed()))
    }

    @Test
    fun changingItemLists() {
        /* Given */
        rule.onUiThread { container.items = listOf(NoteItem(id = 3, text = "Hello!")) }

        /* When */
        rule.onUiThread { container.items = listOf(NoteItem(id = 4, text = "World!")) }
        sleep(300) // ugh
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        /* Then */
        onView(withText("Hello!")).check(doesNotExist())
        onView(withText("World!")).check(matches(isDisplayed()))
    }

    @Test
    fun createClicks() {
        /* Given */
        val observer = rule.container.createClicks.subscribeOn(AndroidSchedulers.mainThread()).test()

        /* When */
        onView(withId(R.id.createButton)).perform(click())

        /* Then */
        expect(observer.valueCount()).toBe(1)
    }

    @Test
    fun itemClicks() {
        /* Given */
        val noteItem = NoteItem(id = 3, text = "Hello!")
        rule.onUiThread { container.items = listOf(noteItem) }

        val observer = rule.container.itemClicks.subscribeOn(AndroidSchedulers.mainThread()).test()

        /* When */
        onView(withText("Hello!")).perform(click())

        /* Then */
        expect(observer.lastValue).toBe(noteItem)
    }

    @Test
    fun deleteClicks() {
        /* Given */
        val noteItem = NoteItem(id = 3, text = "Hello!")
        rule.onUiThread { container.items = listOf(noteItem) }

        val observer = rule.container.deleteClicks.subscribeOn(AndroidSchedulers.mainThread()).test()

        /* When */
        onView(withId(R.id.deleteButton)).perform(click())

        /* Then */
        expect(observer.lastValue).toBe(noteItem)
    }
}