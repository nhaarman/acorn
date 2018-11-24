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

package com.nhaarman.acorn.samples.hellostartactivity

import android.content.Intent
import android.net.Uri
import com.nhaarman.acorn.android.presentation.ActivityController

class MapsActivityController : MapsContainer, ActivityController {

    private var done = false

    private var listeners = listOf<() -> Unit>()

    override fun createIntent(): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://www.google.com/maps/search/?api=1&query=taj+mahal+agra")
        }
    }

    override fun addFinishedEventListener(f: () -> Unit) {
        listeners += f
        if (done) f()
    }

    override fun removeFinishedEventListener(f: () -> Unit) {
        listeners -= f
    }

    override fun onResult(resultCode: Int, data: Intent?) {
        done = true
        listeners.forEach { it.invoke() }
    }
}