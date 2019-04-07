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

package com.nhaarman.acorn.android.navigation

import android.app.Activity
import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.state.NavigatorState

/**
 * An interface that provides a [Navigator] instance to be used in the [Activity].
 *
 * Implementations generally need to cache a created Navigator to deliver the
 * same instance to multiple Activities.
 */
interface NavigatorProvider {

    /**
     * Returns the [Navigator] instance to work with.
     * If one needs to be created, given [savedState] can be used to restore
     * any state, if applicable.
     *
     * @param savedState If not null, the saved state for the Navigator as returned
     * by [saveNavigatorState].
     */
    fun navigatorFor(savedState: NavigatorState?): Navigator

    /**
     * Unlike [navigatorFor], this function should always return a _new_
     * [Navigator] instance.
     *
     * @param savedState If not null, the saved state for the Navigator as returned
     * by [saveNavigatorState].
     */
    fun newInstance(savedState: NavigatorState?): Navigator

    /**
     * Returns the saved state for the [Navigator] as returned by [navigatorFor].
     * Implementations can add extra data to the resulting state to suit their
     * needs.
     */
    fun saveNavigatorState(): NavigatorState?
}
