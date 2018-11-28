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

package com.nhaarman.circleci.builds

import arrow.core.Either
import com.nhaarman.circleci.Build
import io.reactivex.Observable

interface BuildProvider {

    val build: Observable<Either<CacherError<Build>, Build>>

    fun refresh()

    companion object {

        fun create(buildNumber: Int, recentBuildsProvider: RecentBuildsProvider): BuildProvider {
            return DefaultBuildProvider(buildNumber, recentBuildsProvider)
        }
    }
}

internal class DefaultBuildProvider(
    private val buildNumber: Int,
    private val recentBuildsProvider: RecentBuildsProvider
) : BuildProvider {

    override val build: Observable<Either<CacherError<Build>, Build>> by lazy {
        recentBuildsProvider.builds
            .map {
                it.bimap(
                    { a -> CacherError(a.error, a.cachedValue?.firstOrNull { it.buildNumber == buildNumber }) },
                    { a -> a.first { it.buildNumber == buildNumber } }
                )
            }
            .replay(1).refCount()
    }

    override fun refresh() {
        recentBuildsProvider.refresh()
    }
}