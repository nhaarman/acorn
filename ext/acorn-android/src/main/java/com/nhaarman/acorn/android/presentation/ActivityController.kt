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

package com.nhaarman.bravo.android.presentation

import android.app.Activity
import android.content.Intent
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

/**
 * A [Container] specialization that can be used to dispatch [Scene]s as Activities.
 */
interface ActivityController : Container {

    /**
     * Creates the [Intent] that can be used to start the [Activity].
     */
    fun createIntent(): Intent

    /**
     * Called when the [Activity] started with the [Intent] provided by
     * [createIntent] finishes.
     */
    fun onResult(resultCode: Int, data: Intent?)
}