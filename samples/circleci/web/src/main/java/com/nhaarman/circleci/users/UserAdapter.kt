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
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

internal object UserAdapter {

    @FromJson
    fun fromJson(json: UserJson): User {
        return User(
            id = json.login,
            name = json.name,
            avatarUrl = json.avatarUrl
        )
    }

    @ToJson
    fun toJson(user: User): String = error("Unsupported")

    @JsonClass(generateAdapter = true)
    internal class UserJson(
        @Json(name = "login") val login: String,
        @Json(name = "name") val name: String,
        @Json(name = "avatar_url") val avatarUrl: String?
    )
}