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

package com.nhaarman.acorn.android.presentation

import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.test.platform.app.InstrumentationRegistry
import com.nhaarman.acorn.android.presentation.internal.InflatingViewControllerFactory
import com.nhaarman.acorn.android.test.R
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey
import com.nhaarman.expect.expect
import org.junit.Test

internal class InflatingViewControllerFactoryTest {

    @Test
    fun properViewIsReturned() {
        /* Given */
        val viewGroup = FrameLayout(InstrumentationRegistry.getInstrumentation().context)
        val factory = InflatingViewControllerFactory<View>(R.layout.linearlayout) {
            MyContainer(it)
        }

        /* When */
        val result = factory.viewControllerFor(TestScene(SceneKey("test")), viewGroup)

        /* Then */
        expect(result.view).toBeInstanceOf<LinearLayout>()
    }

    @Test
    fun properContainerIsReturned() {
        /* Given */
        val viewGroup = FrameLayout(InstrumentationRegistry.getInstrumentation().context)
        val factory = InflatingViewControllerFactory<View>(R.layout.linearlayout) {
            MyContainer(it)
        }

        /* When */
        val result = factory.viewControllerFor(TestScene(SceneKey("test")), viewGroup)

        /* Then */
        expect(result).toBeInstanceOf<MyContainer> {
            expect(it.view).toBe(result.view)
        }
    }

    private class MyContainer(override val view: View) : ViewController
    private class TestScene(override val key: SceneKey) : Scene<Container>
}
