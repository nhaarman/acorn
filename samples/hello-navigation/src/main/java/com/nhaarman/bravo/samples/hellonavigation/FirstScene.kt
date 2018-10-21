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

package com.nhaarman.bravo.samples.hellonavigation

import android.view.View
import com.nhaarman.bravo.android.presentation.RestorableViewController
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.SaveableScene
import com.nhaarman.bravo.presentation.Scene
import kotlinx.android.synthetic.main.first_scene.*

/**
 * A Scene that shows a button to navigate to the second Scene.
 *
 * This Scene exposes the [FirstScene.Events] callback interface to let the
 * [Navigator] know the second Scene is requested. The Navigator implements this
 * interface to decide whether to make a transition.
 *
 * This Scene does not handle any state restoration, since there is no state
 * worth saving.
 * In cases where state _is_ worth saving, your Scene should generally implement
 * [SaveableScene].
 */
class FirstScene(
    /** The listener callback to be notified when an event happens. */
    private val listener: Events
) : Scene<FirstSceneContainer> {

    override fun attach(v: FirstSceneContainer) {
        v.onSecondSceneClicked { listener.secondSceneRequested() }
    }

    /**
     * An interface to let users of this Scene know something happened.
     */
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