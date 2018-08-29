package com.nhaarman.bravo

/**
 * The logger instance Bravo uses to log statements.
 *
 * By default, Bravo will not log anything to logcat.
 * To enable logging for Bravo, initialize this value with an implementation of [Logger].
 */
var logger: Logger? = null

/**
 *  An interface to facilitate logging with several levels.
 */
interface Logger {

    /** Log a verbose message with given tag. */
    fun v(tag: String, message: Any?)

    /** Log a debug message with given tag. */
    fun d(tag: String, message: Any?)

    /** Log an informational message with given tag. */
    fun i(tag: String, message: Any?)

    /** Log a warning message with given tag. */
    fun w(tag: String, message: Any?)

    /** Log an error message with given tag. */
    fun e(tag: String, message: Any?)
}