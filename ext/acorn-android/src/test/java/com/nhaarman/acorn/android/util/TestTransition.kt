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

package com.nhaarman.bravo.android.util

import android.view.ViewGroup
import com.nhaarman.bravo.android.presentation.ViewController
import com.nhaarman.bravo.android.transition.Transition

class TestTransition : Transition {

    private var callback: Transition.Callback? = null
        set(value) {
            if (field != null) error("Transition already executed")
            field = value
        }

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        this.callback = callback
    }

    fun isStarted() = callback != null

    fun attach(viewController: ViewController) {
        callback!!.attach(viewController)
    }

    fun complete(viewController: ViewController) {
        callback!!.onComplete(viewController)
    }
}