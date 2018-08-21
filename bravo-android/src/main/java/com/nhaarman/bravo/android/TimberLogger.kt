package com.nhaarman.bravo.android

import com.nhaarman.bravo.Logger
import timber.log.Timber

/**
 * A [Logger] implementation that delegates to [Timber].
 *
 * The Timber dependency is not provided by this library, so you will have to
 * include this yourself. Furthermore, no [timber.log.Timber.Tree] instances
 * are planted.
 */
class TimberLogger : Logger {

    override fun v(tag: String, message: Any?) {
        Timber.tag(tag).v("$message")
    }

    override fun d(tag: String, message: Any?) {
        Timber.tag(tag).d("$message")
    }

    override fun i(tag: String, message: Any?) {
        Timber.tag(tag).i("$message")
    }

    override fun w(tag: String, message: Any?) {
        Timber.tag(tag).w("$message")
    }

    override fun e(tag: String, message: Any?) {
        Timber.tag(tag).e("$message")
    }
}