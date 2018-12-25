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

import android.view.ViewGroup
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewProvidingScene
import com.nhaarman.acorn.android.util.inflate
import com.nhaarman.acorn.presentation.SavableScene
import com.nhaarman.acorn.presentation.Scene

/**
 * A simple "Hello, World!" [Scene] implementation.
 *
 * This Scene does not handle any state restoration, since there is no state
 * worth saving.
 * In cases where state _is_ worth saving, your Scene should generally implement
 * [SavableScene].
 */
class HelloWorldScene : ViewProvidingScene<HelloWorldContainer> {

    override fun createViewController(parent: ViewGroup): ViewController {
        return HelloWorldViewController(parent.inflate(R.layout.hello_world))
    }

    override fun attach(v: HelloWorldContainer) {
        v.text = "Hello, world!"
    }
}