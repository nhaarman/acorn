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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nhaarman.circleci.ui.R
import com.nhaarman.circleci.util.getColorCompat

class DashboardSwipeRefreshLayout(
    context: Context,
    attrs: AttributeSet?
) : SwipeRefreshLayout(context, attrs) {

    override fun onFinishInflate() {
        super.onFinishInflate()

        setColorSchemeColors(
            context.getColorCompat(R.color.grey_light),
            context.getColorCompat(R.color.grey_dark)
        )
    }
}