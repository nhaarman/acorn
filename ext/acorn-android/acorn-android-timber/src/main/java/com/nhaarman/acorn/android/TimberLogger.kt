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

package com.nhaarman.acorn.android

import com.nhaarman.acorn.Logger
import timber.log.Timber

/**
 * A [Logger] implementation that delegates to [Timber].
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
