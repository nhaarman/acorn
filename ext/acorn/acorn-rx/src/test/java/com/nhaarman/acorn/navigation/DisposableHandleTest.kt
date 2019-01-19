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

import com.nhaarman.expect.expect
import org.junit.jupiter.api.Test

class DisposableHandleTest {

    @Test
    fun `resulting disposable mirrors non disposed state`() {
        /* Given */
        val handle = SimpleDisposableHandle()

        /* When */
        val disposable = handle.asDisposable()

        /* Then */
        expect(disposable.isDisposed).toBe(false)
    }

    @Test
    fun `resulting disposable mirrors disposed state`() {
        /* Given */
        val handle = SimpleDisposableHandle()

        /* When */
        val disposable = handle.asDisposable()
        handle.dispose()

        /* Then */
        expect(disposable.isDisposed).toBe(true)
    }

    @Test
    fun `resulting disposable delegates disposing to handle`() {
        /* Given */
        val handle = SimpleDisposableHandle()

        /* When */
        val disposable = handle.asDisposable()
        disposable.dispose()

        /* Then */
        expect(handle.isDisposed()).toBe(true)
    }

    private class SimpleDisposableHandle : DisposableHandle {

        private var isDisposed = false

        override fun dispose() {
            isDisposed = true
        }

        override fun isDisposed(): Boolean {
            return isDisposed
        }
    }
}
