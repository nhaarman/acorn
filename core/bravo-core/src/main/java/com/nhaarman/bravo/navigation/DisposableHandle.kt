package com.nhaarman.bravo.navigation

/**
 * A handle that can be disposed of.
 */
interface DisposableHandle {

    /**
     * Disposes the resource.
     */
    fun dispose()

    /**
     * @return true if this handle has been disposed of.
     */
    fun isDisposed(): Boolean
}