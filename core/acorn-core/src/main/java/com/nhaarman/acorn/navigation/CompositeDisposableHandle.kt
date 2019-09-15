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

package com.nhaarman.acorn.navigation

/**
 * A non-thread-safe [DisposableHandle] implementation that can serve
 * as a container for multiple [DisposableHandle] instances.
 */
class CompositeDisposableHandle : DisposableHandle {

    private var isDisposed = false

    private var bag = setOf<DisposableHandle>()

    operator fun plusAssign(d: DisposableHandle) {
        if (isDisposed) {
            d.dispose()
            return
        }

        bag += d
    }

    override fun dispose() {
        isDisposed = true
        bag.forEach { it.dispose() }
    }

    /**
     * Disposes all the [DisposableHandle] instances this CompositeDisposableHandle
     * contains, but does not dispose of this CompositeDisposableHandle itself.
     */
    fun clear() {
        bag.forEach { it.dispose() }
        bag = emptySet()
    }

    override fun isDisposed(): Boolean {
        return isDisposed
    }
}
