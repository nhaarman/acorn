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
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.state.get
import com.nhaarman.acorn.state.sceneState

/**
 * An abstract basic [Scene] implementation that provides commonly used
 * functionality.
 *
 * This class provides an [attachedView] property which provides the currently
 * attached [V] instance, if available.
 *
 * If the [Container] type [V] implements [RestorableContainer], this class
 * will save and restore the view state between subsequent calls to [attach] and
 * [detach].
 *
 * This class is able to save and restore its instance state in
 * [saveInstanceState], but does not implement [SavableScene] itself.
 * You can opt in to this state saving by explicitly implementing the
 * [SavableScene] interface.
 *
 * @param V The view type for this [Scene]. Can implement [RestorableContainer]
 * to save and restore view state between different views attached to the Scene.
 * @param savedState A previous saved state instance for this [Scene] as
 * returned by [saveInstanceState]. May be `null`.
 * @constructor Creates a new [BasicScene], restoring view state when
 * available.
 */
abstract class BasicScene<V : Container>(
    savedState: SceneState?
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

    private var containerState: ContainerState? = savedState?.containerState

    @CallSuper
    override fun attach(v: V) {
        containerState?.let { (v as? RestorableContainer)?.restoreInstanceState(it) }
        containerState = null
        attachedView = v
    }

    @CallSuper
    override fun detach(v: V) {
        containerState = (v as? RestorableContainer)?.saveInstanceState()
        attachedView = null
    }

    /**
     * Saves the instance state for this Scene.
     *
     * The default implementation of this method will save the view state if [V]
     * implements [RestorableContainer].
     *
     * Implementers of this class may override this method to save additional
     * information.
     * However, it is recommended to call the default implementation.
     */
    @CallSuper
    open fun saveInstanceState(): SceneState {
        return sceneState {
            it.containerState = containerState
                ?: (attachedView as? RestorableContainer)?.saveInstanceState()
        }
    }

    companion object {

        private var SceneState.containerState: ContainerState?
            set(value) {
                this["view_state"] = value
            }
            get() = get("view_state")
    }
}
