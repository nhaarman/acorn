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

package com.nhaarman.circleci.dashboard.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.nhaarman.circleci.Build
import com.nhaarman.circleci.Build.Status.Canceled
import com.nhaarman.circleci.Build.Status.Failed
import com.nhaarman.circleci.Build.Status.Fixed
import com.nhaarman.circleci.Build.Status.NotRunning
import com.nhaarman.circleci.Build.Status.Queued
import com.nhaarman.circleci.Build.Status.Running
import com.nhaarman.circleci.Build.Status.Scheduled
import com.nhaarman.circleci.Build.Status.Success
import com.nhaarman.circleci.ui.R

class BuildStatusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var status: Build.Status? = null
        set(value) {
            val colorResource = when (value) {
                Scheduled, Queued -> R.color.purple
                Running -> R.color.blue
                Success, Fixed -> R.color.green
                Failed -> R.color.red
                NotRunning, Canceled, null -> R.color.grey_light
            }

            setBackgroundResource(colorResource)
        }
}