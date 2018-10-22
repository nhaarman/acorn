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

package com.nhaarman.bravo.android.presentation

import android.view.View
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

/**
 * A [Container] specialization that acts as a wrapper around a [View].
 *
 * The term 'ViewController' here is used in the sense that it acts as a bridge
 * between a [Scene] and a [View], being some sort of 'controller' of the view.
 *
 * When creating a [ViewController], the [view] property must reference the root
 * of the scene layout.
 */
interface ViewController : Container {

    /**
     * The root [View] of the scene that can be used to control the contents
     * of the View.
     *
     * This property must return the root of the scene layout.
     */
    val view: View
}