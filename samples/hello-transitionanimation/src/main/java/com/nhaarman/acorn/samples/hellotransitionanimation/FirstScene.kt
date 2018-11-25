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

package com.nhaarman.acorn.samples.hellotransitionanimation

import android.view.View
import android.view.ViewGroup
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewProvidingScene
import com.nhaarman.acorn.android.util.inflate
import com.nhaarman.acorn.presentation.Container
import kotlinx.android.synthetic.main.first_scene.*

class FirstScene(
    private val listener: Events
) : ViewProvidingScene<FirstSceneContainer> {

    override fun createViewController(parent: ViewGroup): ViewController {
        return FirstSceneViewController(parent.inflate(R.layout.first_scene))
    }

    override fun attach(v: FirstSceneContainer) {
        v.onSecondSceneClicked { listener.secondSceneRequested() }
    }

    interface Events {

        fun secondSceneRequested()
    }
}

interface FirstSceneContainer : Container {

    fun onSecondSceneClicked(f: () -> Unit)
}

class FirstSceneViewController(
    override val view: View
) : RestorableViewController, FirstSceneContainer {

    override fun onSecondSceneClicked(f: () -> Unit) {
        secondSceneButton.setOnClickListener { f() }
    }
}