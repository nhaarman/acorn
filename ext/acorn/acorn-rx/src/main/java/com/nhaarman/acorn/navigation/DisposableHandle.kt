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

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Adds given [disposableHandle] as a [Disposable] to the receiving
 * [CompositeDisposable].
 */
operator fun CompositeDisposable.plusAssign(disposableHandle: DisposableHandle) {
    add(disposableHandle.asDisposable())
}

/**
 * Wraps the receiving [DisposableHandle] as a [Disposable].
 */
fun DisposableHandle.asDisposable() = object : Disposable {
    override fun isDisposed(): Boolean {
        return this@asDisposable.isDisposed()
    }

    override fun dispose() {
        this@asDisposable.dispose()
    }
}