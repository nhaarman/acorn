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

import com.nhaarman.acorn.presentation.RxScene
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.circleci.builds.RecentBuildsProvider
import com.nhaarman.circleci.mainThread
import io.reactivex.rxkotlin.plusAssign

class DashboardScene(
    private val recentBuildsProvider: RecentBuildsProvider,
    savedState: SceneState? = null
) : RxScene<DashboardContainer>(savedState) {

    private val recentBuilds by lazy {
        whenStarted { recentBuildsProvider.builds }
            .observeOn(mainThread)
            .replay(1).autoConnect(this)
    }

    override fun onStart() {
        super.onStart()

        disposables += recentBuilds
            .combineWithLatestView()
            .subscribe { (recentBuilds, container) ->
                container?.recentBuilds = recentBuilds
            }

        disposables += view.whenAvailable { it.refreshRequests }
            .subscribe { recentBuildsProvider.refresh() }
    }
}