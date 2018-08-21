package com.nhaarman.bravo.android.tests

import android.support.test.rule.ActivityTestRule
import com.nhaarman.bravo.android.transition.ViewFactory
import com.nhaarman.bravo.presentation.Container

class BravoViewTestRule<C : Container>(
    private val viewFactory: ViewFactory,
    private val sceneKey: String
) : ActivityTestRule<BravoTestActivity>(BravoTestActivity::class.java) {

    val viewResult by lazy {
        viewFactory.viewFor(sceneKey, activity.findViewById(android.R.id.content))
    }

    @Suppress("UNCHECKED_CAST")
    val container: C
        get() = viewResult.container as C

    override fun afterActivityLaunched() {
        runOnUiThread { activity.setContentView(viewResult.view) }
    }

    fun onUiThread(f: BravoViewTestRule<C>.() -> Unit) {
        runOnUiThread { f(this) }
    }
}