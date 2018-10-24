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

package com.nhaarman.circleci.users

import com.nhaarman.circleci.User
import com.nhaarman.httpmonads.HttpTry
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface UserProvider {

    val user: Observable<HttpTry<User>>

    fun refresh()

    companion object {

        fun create(userService: UserService): UserProvider {
            return WebUserProvider(userService)
        }
    }
}

internal class WebUserProvider(
    private val userService: UserService
) : UserProvider {

    private val refreshSubject = PublishSubject.create<Unit>()
    override val user: Observable<HttpTry<User>> by lazy {
        refreshSubject.startWith(Unit)
            .flatMapSingle { userService.getMe() }
            .scan { previous: HttpTry<User>, current: HttpTry<User> ->
                current or previous
            }
            .replay(1).refCount()
    }

    override fun refresh() {
        refreshSubject.onNext(Unit)
    }

    private infix fun <T> HttpTry<T>.or(other: HttpTry<T>): HttpTry<T> {
        return when (this) {
            is HttpTry.Failure -> other
            is HttpTry.Success -> this
        }
    }
}
