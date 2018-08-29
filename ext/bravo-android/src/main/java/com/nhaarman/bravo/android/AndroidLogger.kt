package com.nhaarman.bravo.android

import android.util.Log
import com.nhaarman.bravo.Logger

/**
 * A [Logger] implementation that uses Android's [Log] class for logging.
 */
class AndroidLogger : Logger {

    override fun v(tag: String, message: Any?) {
        Log.v(tag, "$message")
    }

    override fun d(tag: String, message: Any?) {
        Log.d(tag, "$message")
    }

    override fun i(tag: String, message: Any?) {
        Log.i(tag, "$message")
    }

    override fun w(tag: String, message: Any?) {
        Log.w(tag, "$message")
    }

    override fun e(tag: String, message: Any?) {
        Log.e(tag, "$message")
    }
}