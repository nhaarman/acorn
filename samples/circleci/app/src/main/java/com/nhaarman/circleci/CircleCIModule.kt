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

package com.nhaarman.circleci

import com.nhaarman.circleci.builds.BuildProvider
import com.nhaarman.circleci.builds.RecentBuildsProvider
import com.nhaarman.circleci.web.WebServiceFactory

class CircleCIModule {

    private val webServiceFactory by lazy {
        WebServiceFactory
            .create(
                baseUrl = "https://circleci.com/api/v1.1/",
                apiToken = BuildConfig.CIRCLECI_API_TOKEN
            )
    }

    val component by lazy {
        val recentBuildsProvider = RecentBuildsProvider.create(webServiceFactory.createRecentBuildsService())

        CircleCIComponent(
            recentBuildsProvider,
            { buildNumber -> BuildProvider.create(buildNumber, recentBuildsProvider) }
        )
    }
}