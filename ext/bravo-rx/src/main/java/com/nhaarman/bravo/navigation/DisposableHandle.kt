package com.nhaarman.bravo.navigation

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