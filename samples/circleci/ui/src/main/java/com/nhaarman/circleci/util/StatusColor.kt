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

package com.nhaarman.circleci.util

import com.nhaarman.circleci.Build
import com.nhaarman.circleci.ui.R

fun Build.Status?.toColor() = when (this) {
    Build.Status.Scheduled, Build.Status.Queued -> R.color.purple
    Build.Status.Running -> R.color.blue
    Build.Status.Success, Build.Status.Fixed -> R.color.green
    Build.Status.Failed -> R.color.red
    Build.Status.NotRunning, Build.Status.Canceled, null -> R.color.grey_light
}