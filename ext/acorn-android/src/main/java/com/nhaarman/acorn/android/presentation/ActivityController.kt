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

package com.nhaarman.acorn.android.presentation

import android.app.Activity
import android.content.Intent
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene

/**
 * A [Container] specialization that can be used to dispatch [Scene]s as Activities.
 *
 * Implementers must either implement [createIntent] or [start].
 */
interface ActivityController : Container {

    /**
     * Creates the [Intent] that can be used to start the [Activity].
     *
     * Override this function if you simply want to build an Intent
     * and don't worry about anything else. Use [start] if you want
     * to have more control.
     */
    fun createIntent(): Intent? {
        return null
    }

    /**
     * Starts the designated Activity.
     *
     * Override this function to start the Activity. To be able to
     * call [Activity.startActivityForResult], implementers are
     * responsible to deliver the receiving Activity instance.
     *
     * Implementations should invoke [Activity.startActivityForResult]
     * rather than [Activity.startActivity], to ensure Acorn is notified
     * when the Activity has finished.
     *
     * If you simply want to provide an Intent that should be started
     * for you, override [createIntent].
     */
    fun start() {
        error("Either override createIntent() or start() to start an Activity.")
    }

    /**
     * Called when the [Activity] started with the [Intent] provided by
     * [createIntent] finishes.
     */
    fun onResult(resultCode: Int, data: Intent?) {
        error("You must override onResult")
    }

    /**
     * Called when the [Activity] started with the [Intent] provided by
     * [createIntent] finishes.
     */
    fun onResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onResult(resultCode, data)
    }
}
