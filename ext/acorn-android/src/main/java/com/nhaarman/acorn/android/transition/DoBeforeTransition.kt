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

/**
 * A [SceneTransition] implementation that can execute an action before a delegate
 * SceneTransition is executed.
 *
 * This can be used to hide the keyboard before the transition starts, for
 * example.
 *
 * @see [doOnStart]
 */
class DoBeforeTransition private constructor(
    private val delegate: SceneTransition,
    private val action: (ViewGroup) -> Unit,
) : SceneTransition {

    override fun execute(parent: ViewGroup, callback: SceneTransition.Callback) {
        action(parent)
        delegate.execute(parent, callback)
    }

    companion object {

        fun create(delegate: SceneTransition, action: (parent: ViewGroup) -> Unit): DoBeforeTransition {
            return DoBeforeTransition(delegate, action)
        }
    }
}

/**
 * Returns a [SceneTransition] that runs [action] before the receiving SceneTransition
 * instance is started.
 *
 * @param action The action to run.
 */
fun SceneTransition.doOnStart(action: (parent: ViewGroup) -> Unit): SceneTransition {
    return DoBeforeTransition.create(this, action)
}
