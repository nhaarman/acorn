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

package com.nhaarman.acorn

/**
 * The logger instance Acorn uses to log statements.
 *
 * By default, Acorn will not log anything to logcat.
 * To enable logging for Acorn, initialize this value with an implementation of [Logger].
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