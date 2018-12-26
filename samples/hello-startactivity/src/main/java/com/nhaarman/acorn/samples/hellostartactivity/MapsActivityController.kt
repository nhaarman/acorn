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