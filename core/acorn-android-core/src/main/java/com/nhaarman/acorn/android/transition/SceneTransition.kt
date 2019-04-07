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

package com.nhaarman.acorn.android.transition

import android.view.ViewGroup
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene

/**
 * An interface to manually implement [Scene] transition animations.
 */
interface SceneTransition {

    /**
     * Executes the transition.
     *
     * Implementers of this interface have full control over [parent] and must
     * update its child hierarchy accordingly. That means any old views should
     * be removed and new views must be inflated and added to the [parent].
     *
     * When the transition is done, implementers must always invoke
     * [Callback.onComplete]. Optionally, [Callback.attach] can be invoked to
     * attach the resulting [Container] to the [Scene] before the transition
     * has finished.
     *
     * @param parent The [ViewGroup] that hosts the application UI, with the
     * views from the previous [Scene] still attached.
     * @param callback The [Callback] whose [Callback.onComplete] method must
     * be invoked when the transition is done.
     */
    fun execute(parent: ViewGroup, callback: Callback)

    /**
     * A callback interface to be able to get notified when a [SceneTransition] ends.
     * Implementers of [SceneTransition.execute], must always invoke [onComplete]
     * when the [SceneTransition] is finished.
     * Optionally, [attach] can be invoked during the transition animation to
     * have the view attached to the [Scene] before the animation ends.
     */
    interface Callback {

        /**
         * An function that can optionally  be invoked to attach given
         * [viewController] to the [Scene] at any time during the transition.
         *
         * When invoking this method, this must be done before [onComplete],
         * otherwise its invocation is ignored.
         * If this method is not invoked at all, [onComplete] will take care of
         * attaching the view to the [Scene].
         *
         * @param viewController The [ViewController] which contains the View
         * that is shown and the instance that can be attached to the [Scene].
         */
        fun attach(viewController: ViewController)

        /**
         * Implementers of [SceneTransition.execute] must invoke this method when the
         * transition is finished. If no call to [attach] was made before
         * invoking this method, given [viewController] will be attached to the
         * [Scene].
         */
        fun onComplete(viewController: ViewController)
    }
}
