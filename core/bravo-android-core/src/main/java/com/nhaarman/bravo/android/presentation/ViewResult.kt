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

import android.app.Activity
import android.view.View
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

/**
 * A wrapper class for a Scene's view and container.
 */
class ViewResult private constructor(
    val view: View,
    val container: Container
) {

    companion object {

        /**
         * Creates a new [ViewResult] from given [View].
         *
         * @param view The root view for its [Scene], to be attached to the
         * [Activity] layout. This [View] instance must implement
         * [Container], or an [IllegalArgumentException] is thrown.
         */
        fun from(view: View): ViewResult {
            if (view !is Container) {
                throw IllegalArgumentException("View should implement ${Container::class.java.name}: $view")
            }

            return ViewResult(view = view, container = view)
        }

        /**
         * Creates a new [ViewResult] from given [View] and [Container].
         *
         * @param view The root view for its Scene, to be attached to the
         * [Activity] layout.
         * @param container A [Container] instance that can be passed to the
         * [Scene] and which delegates the [Scene]'s commands
         * to [view].
         */
        fun from(view: View, container: Container): ViewResult {
            return ViewResult(view, container)
        }
    }
}