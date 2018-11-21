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

package com.nhaarman.acorn.samples.helloworld

import android.view.View
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.presentation.Container
import kotlinx.android.synthetic.main.hello_world.*

/** An interface describing the "Hello, world!" view. */
interface HelloWorldContainer : Container {

    var text: String
}

/**
 * A [View] implementation implementing the [HelloWorldContainer].
 */
class HelloWorldViewController(
    override val view: View
) : RestorableViewController, HelloWorldContainer {

    override var text: String = ""
        set(value) {
            textView.text = value
        }
}