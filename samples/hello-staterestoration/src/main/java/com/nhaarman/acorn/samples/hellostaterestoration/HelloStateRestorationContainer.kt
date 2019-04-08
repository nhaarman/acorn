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

package com.nhaarman.acorn.samples.hellostaterestoration

import android.view.View
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.presentation.RestorableContainer
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.myscene.*

/**
 * An interface describing the view.
 *
 * Implements [RestorableContainer] to be able to save and restore its state.
 */
interface HelloStateRestorationContainer : RestorableContainer {

    /**
     * A counter value to be shown.
     */
    var counterValue: Int

    /**
     * Register
     */
    fun onNextClicked(f: () -> Unit)
}

/**
 * A [ViewController] implementation implementing the
 * [HelloStateRestorationContainer].
 *
 * Implements [RestorableViewController] to use a default implementation of
 * saving and restoring view state.
 */
class HelloStateRestorationViewController(
    override val view: View
) : HelloStateRestorationContainer,
    RestorableViewController,
    LayoutContainer {

    override var counterValue: Int = 0
        set(value) {
            counterTV.text = "$value"
        }

    override fun onNextClicked(f: () -> Unit) {
        nextButton.setOnClickListener { f.invoke() }
    }

    override val containerView = view
}
