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

package com.nhaarman.bravo.samples.hellostaterestoration

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.nhaarman.bravo.android.presentation.RestorableView
import com.nhaarman.bravo.presentation.RestorableContainer
import kotlinx.android.synthetic.main.myscene.view.*

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
 * A [View] implementation implementing the [HelloStateRestorationContainer].
 *
 * Implements [RestorableView] to use a default implementation of saving and
 * restoring view state.
 */
class HelloStateRestorationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    HelloStateRestorationContainer,
    RestorableView {

    override var counterValue: Int = 0
        set(value) {
            counterTV.text = "$value"
        }

    override fun onNextClicked(f: () -> Unit) {
        nextButton.setOnClickListener { f.invoke() }
    }
}
