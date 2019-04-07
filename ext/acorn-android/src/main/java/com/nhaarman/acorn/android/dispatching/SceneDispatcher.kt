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

package com.nhaarman.acorn.android.dispatching

import android.app.Activity
import android.content.Intent
import androidx.annotation.CheckResult
import com.nhaarman.acorn.navigation.DisposableHandle
import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.SavedState

/**
 * A [SceneDispatcher] is responsible for bridging between [Activity] instances
 * and the [Navigator], handling changes of [Scene] instances, and ensuring the
 * UI is properly updated.
 */
interface SceneDispatcher {

    /**
     * Starts dispatching the [Scene] instances for given [navigator].
     *
     * By disposing the resulting [DisposableHandle] one can stop the
     * dispatching.
     */
    @CheckResult
    fun dispatchScenesFor(navigator: Navigator): DisposableHandle

    /**
     * To be invoked when the application's UI becomes visible to the user.
     * A good place to invoke this would be in [Activity.onStart].
     */
    fun onUIVisible()

    /**
     * To be invoked when the application's UI becomes invisible to the user.
     * A good place to invoke this would be in [Activity.onStop].
     */
    fun onUINotVisible()

    /**
     * To be invoked when the [Activity] receives an invocation to its
     * [Activity.onActivityResult] method.
     */
    fun onActivityResult(resultCode: Int, data: Intent?)

    /**
     * Saves any state for this [SceneDispatcher].
     */
    fun saveInstanceState(): SavedState
}
