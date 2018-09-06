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

package com.nhaarman.bravo.android.transition

import android.view.ViewGroup
import com.nhaarman.bravo.android.presentation.ViewResult
import com.nhaarman.bravo.presentation.Scene

/**
 * An interface to manually implement Scene transition animations.
 */
interface Transition {

    /**
     * Executes the transition.
     *
     * Implementers of this interface have full control over [parent] and must
     * update its child hierarchy accordingly. That means any old views should
     * be removed and new views must be inflated and added to the [parent].
     *
     * When the transition is done, implementers must always invoke
     * [Callback.onComplete].
     *
     * @param parent The [ViewGroup] that hosts the application UI, with the
     * views from the previous [Scene] still attached.
     * @param callback The [Callback] whose [Callback.onComplete] method must
     * be invoked when the transition is done.
     */
    fun execute(parent: ViewGroup, callback: Callback)

    /**
     * A callback interface to be able to get notified when a [Transition] ends.
     * Implementers of [Transition.execute], must always invoke [onComplete]
     * when the [Transition] is finished.
     * Optionally, [attach] can be invoked during the transition animation to
     * have the view attached to the [Scene] before the animation ends.
     */
    interface Callback {

        /**
         * An optional function to attach given [viewResult] to the [Scene] at
         * any time during the transition.
         *
         * When invoking this method, this must be done before [onComplete],
         * otherwise its invocation is ignored.
         * If this method is not invoked at all, [onComplete] will take care of
         * attaching the view to the [Scene].
         *
         * @param viewResult The [ViewResult] which contains the View that is
         * shown and the instance that can be attached to the [Scene].
         */
        fun attach(viewResult: ViewResult)

        /**
         * Implementers of [Transition.execute] must invoke this method when the
         * transition is finished. If no call to [attach] was made before
         * invoking this method, given [viewResult] will be attached to the
         * [Scene].
         */
        fun onComplete(viewResult: ViewResult)
    }
}