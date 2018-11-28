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
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

interface RecentBuildsProvider {

    val builds: Observable<Either<CacherError<List<Build>>, List<Build>>>

    fun refresh()

    companion object {

        fun create(recentBuildsService: RecentBuildsService): RecentBuildsProvider {
            return WebRecentBuildsProvider(recentBuildsService, Cacher())
        }
    }
}

internal class WebRecentBuildsProvider(
    private val recentBuildsService: RecentBuildsService,
    private val cacher: Cacher<List<Build>>
) : RecentBuildsProvider {

    private val refreshSubject = PublishSubject.create<Unit>()

    private val refreshRequests by lazy {
        refreshSubject
            .startWith(Unit)
            .switchMap {
                Observable.interval(0, 10, TimeUnit.SECONDS)
            }
    }

    override val builds: Observable<Either<CacherError<List<Build>>, List<Build>>> by lazy {
        cacher
            .cache(
                refreshRequests.flatMapSingle { recentBuildsService.retrieveRecentBuilds() }
            )
            .replay(1).refCount(1, TimeUnit.SECONDS)
    }

    override fun refresh() {
        refreshSubject.onNext(Unit)
    }
}