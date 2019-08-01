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
