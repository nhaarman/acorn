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

package com.nhaarman.circleci.web

import com.nhaarman.circleci.builds.RecentBuildsService
import com.nhaarman.circleci.builds.RetrofitRecentBuildsApi
import com.nhaarman.circleci.builds.RetrofitRecentBuildsService
import com.nhaarman.circleci.projects.ProjectsService
import com.nhaarman.circleci.projects.RetrofitProjectsApi
import com.nhaarman.circleci.projects.RetrofitProjectsService
import com.nhaarman.httpmonads.RxHttpMonadsCallAdapterFactory
import retrofit2.Retrofit

interface WebServiceFactory {

    fun createProjectsService(): ProjectsService
    fun createRecentBuildsService(): RecentBuildsService

    companion object {

        fun create(baseUrl: String, apiToken: String): WebServiceFactory {
            return DefaultWebServiceFactory(baseUrl, HttpClientFactory(apiToken))
        }
    }
}

internal class DefaultWebServiceFactory(
    private val baseUrl: String,
    private val httpClientFactory: HttpClientFactory
) : WebServiceFactory {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxHttpMonadsCallAdapterFactory.create())
            .client(httpClientFactory.createHttpClient())
            .build()
    }

    override fun createProjectsService(): ProjectsService {
        return RetrofitProjectsService(RetrofitProjectsApi.create(retrofit))
    }

    override fun createRecentBuildsService(): RecentBuildsService {
        return RetrofitRecentBuildsService(RetrofitRecentBuildsApi.create(retrofit))
    }
}