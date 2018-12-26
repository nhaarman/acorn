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