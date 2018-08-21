package com.nhaarman.bravo.notesapp.android.ui.createitem

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withHint
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import com.nhaarman.bravo.android.tests.BravoViewTestRule
import com.nhaarman.bravo.notesapp.android.R
import com.nhaarman.bravo.notesapp.android.ViewFactoryProvider
import com.nhaarman.bravo.notesapp.presentation.createitem.CreateItemContainer
import com.nhaarman.bravo.notesapp.presentation.createitem.CreateItemScene
import com.nhaarman.expect.expect
import com.nhaarman.expect.lastValue
import io.reactivex.android.schedulers.AndroidSchedulers
import org.junit.Rule
import org.junit.Test

class CreateItemViewTest {

    @Rule @JvmField val rule = BravoViewTestRule<CreateItemContainer>(
        ViewFactoryProvider.viewFactory,
        CreateItemScene.key
    )

    @Test
    fun settingInitialText() {
        /* When */
        rule.onUiThread { container.setInitialText("Hello!") }

        /* Then */
        onView(withText("Hello!")).check(matches(isDisplayed()))
    }

    @Test
    fun inputText() {
        /* Given */
        val observer = rule.container.textChanges.test()

        /* When */
        onView(withHint("Take a note")).perform(typeText("Hello, world!"))

        /* Then */
        expect(observer.lastValue).toBe("Hello, world!")
    }

    @Test
    fun clickCreate() {
        /* Given */
        val observer = rule.container.createClicks.subscribeOn(AndroidSchedulers.mainThread()).test()

        /* When */
        onView(withId(R.id.save)).perform(click())

        /* Then */
        expect(observer.valueCount()).toBe(1)
    }
}
