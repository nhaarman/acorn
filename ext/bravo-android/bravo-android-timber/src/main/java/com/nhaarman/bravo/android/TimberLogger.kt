/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.android

import com.nhaarman.bravo.Logger
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