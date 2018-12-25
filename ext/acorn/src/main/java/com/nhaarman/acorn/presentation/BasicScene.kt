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

package com.nhaarman.acorn.presentation

import androidx.annotation.CallSuper
import com.nhaarman.acorn.state.ContainerState

/**
 * A basic abstract [Scene] implementation that provides some basic functionality.
 *
 * This class provides an [attachedView] property which provides the currently
 * attached [V] instance, if available.
 *
 * @param V The view type for this [Scene]. Can implement [RestorableContainer]
 * to save and restore view state between different views attached to the Scene.
 * @property containerState The initial view state for this [Scene].
 * May be `null`.
 * @constructor Creates a new [BasicScene], restoring view state when available.
 */
abstract class BasicScene<V : Container>(
    private var containerState: ContainerState? = null
) : Scene<V> {

    /**
     * The currently attached [V] instance, if available.
     *
     * This property will be updated when [attach] or [detach] is called.
     *
     * Returns `null` if no instance is attached.
     */
    protected var attachedView: V? = null
        private set

    @CallSuper
    override fun attach(v: V) {
        containerState?.let { (v as? RestorableContainer)?.restoreInstanceState(it) }
        attachedView = v
    }

    @CallSuper
    override fun detach(v: V) {
        containerState = (v as? RestorableContainer)?.saveInstanceState()
        attachedView = null
    }
}