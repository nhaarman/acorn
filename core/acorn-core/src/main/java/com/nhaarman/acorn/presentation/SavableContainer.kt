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

import com.nhaarman.acorn.state.ContainerState

/**
 * Indicates that implementers can have their instance state saved.
 */
interface SavableContainer : Container {

    /**
     * Save instance state.
     */
    fun saveInstanceState(): ContainerState
}

/**
 * Indicates that implementers can have their instance state saved and restored.
 */
interface RestorableContainer : SavableContainer {

    /** Restore given instance state. */
    fun restoreInstanceState(bundle: ContainerState)
}