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