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

package com.nhaarman.bravo.samples.hellotransitionanimation

import android.view.View
import com.nhaarman.bravo.android.presentation.RestorableViewController
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import kotlinx.android.synthetic.main.first_scene.*

class FirstScene(
    private val listener: Events
) : Scene<FirstSceneContainer> {

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