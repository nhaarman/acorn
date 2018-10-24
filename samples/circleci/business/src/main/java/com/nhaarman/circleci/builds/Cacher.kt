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
import com.nhaarman.httpmonads.HttpError
import com.nhaarman.httpmonads.HttpTry
import io.reactivex.Observable

data class CacherError<T>(
    val error: HttpError?,
    val cachedValue: T?
)

class Cacher<T> {

    fun cache(source: Observable<HttpTry<T>>): Observable<Either<CacherError<T>, T>> {
        return source
            .scan<Either<CacherError<T>, T>>(
                Either.left(CacherError<T>(null, null))
            ) { previous, current -> resolve(previous, current) }
            .skip(1)
            .replay(1).refCount()
    }

    private fun resolve(
        previous: Either<CacherError<T>, T>,
        current: HttpTry<T>
    ): Either<CacherError<T>, T> {
        return when (current) {
            is HttpTry.Failure -> Either.left(
                resolve(
                    previous,
                    current.httpError
                )
            )
            is HttpTry.Success -> Either.right(current.value)
        }
    }

    private fun resolve(previous: Either<CacherError<T>, T>, error: HttpError): CacherError<T> {
        return previous
            .fold(
                { dashboardError ->
                    CacherError(error, dashboardError.cachedValue)
                },
                { dashboard ->
                    CacherError(error, dashboard)
                }
            )
    }
}