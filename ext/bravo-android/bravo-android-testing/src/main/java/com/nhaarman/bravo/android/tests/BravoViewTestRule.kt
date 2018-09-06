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

package com.nhaarman.bravo.android.tests

import android.support.test.rule.ActivityTestRule
import com.nhaarman.bravo.android.presentation.ViewFactory
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.SceneKey

class BravoViewTestRule<C : Container>(
    private val viewFactory: ViewFactory,
    private val sceneKey: SceneKey
) : ActivityTestRule<BravoTestActivity>(BravoTestActivity::class.java) {

    val viewResult by lazy {
        viewFactory.viewFor(sceneKey, activity.findViewById(android.R.id.content))
            ?: error("No view could be created for Scene with key $sceneKey.")
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