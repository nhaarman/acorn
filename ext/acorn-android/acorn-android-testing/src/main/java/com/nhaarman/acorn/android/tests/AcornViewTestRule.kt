/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nhaarman.acorn.android.tests

import androidx.test.rule.ActivityTestRule
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey

class AcornViewTestRule<C : Container>(
    private val viewControllerFactory: ViewControllerFactory,
    private val sceneKey: SceneKey,
) : ActivityTestRule<AcornTestActivity>(AcornTestActivity::class.java) {

    private val viewController by lazy {
        viewControllerFactory.viewControllerFor(TestScene(sceneKey), activity.findViewById(android.R.id.content))
    }

    @Suppress("UNCHECKED_CAST")
    val container: C
        get() = viewController as C

    override fun afterActivityLaunched() {
        runOnUiThread { activity.setContentView(viewController.view) }
    }

    fun onUiThread(f: AcornViewTestRule<C>.() -> Unit) {
        runOnUiThread { f(this) }
    }

    private inner class TestScene(override val key: SceneKey) : Scene<C>
}
