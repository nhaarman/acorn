/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
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