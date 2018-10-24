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

package com.nhaarman.circleci.dashboard

import android.view.View
import arrow.core.Either
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.circleci.Build
import com.nhaarman.circleci.builds.CacherError
import io.reactivex.Observable
import kotlinx.android.synthetic.main.dashboard_scene.*

class DashboardViewController(
    override val view: View
) : DashboardContainer, RestorableViewController {

    override var recentBuilds: Either<CacherError<List<Build>>, List<Build>>? = null
        set(value) {
            swipeRefreshLayout.isRefreshing = false

            recentBuildsRV.recentBuilds = when (value) {
                is Either.Left -> value.a.cachedValue ?: emptyList()
                is Either.Right -> value.b
                null -> emptyList()
            }
        }

    override val refreshRequests: Observable<Unit>
        get() = swipeRefreshLayout.refreshes()
}