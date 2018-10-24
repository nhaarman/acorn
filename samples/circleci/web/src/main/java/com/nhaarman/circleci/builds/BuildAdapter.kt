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

import com.nhaarman.circleci.Build
import com.nhaarman.circleci.User
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.ToJson
import org.threeten.bp.ZonedDateTime

internal object BuildAdapter {

    @FromJson
    fun fromJson(json: BuildJson): Build {
        return Build(
            buildNumber = json.buildNumber,
            subject = json.subject,
            repoName = json.repoName,
            branchName = json.branchName,
            status = json.status.asBuildStatus(),
            user = json.user,
            queuedAt = json.queuedAt?.let { ZonedDateTime.parse(it) }
        )
    }

    private fun String.asBuildStatus(): Build.Status {
        return when (this) {
            "scheduled" -> Build.Status.Scheduled
            "queued" -> Build.Status.Queued
            "not_running" -> Build.Status.NotRunning
            "running" -> Build.Status.Running
            "success" -> Build.Status.Success
            "fixed" -> Build.Status.Fixed
            "failed" -> Build.Status.Failed
            "canceled" -> Build.Status.Canceled
            else -> throw JsonDataException("Unknown status: $this")
        }
    }

    @ToJson
    fun toJson(build: Build): String = error("Unsupported")

    @JsonClass(generateAdapter = true)
    internal class BuildJson(
        @Json(name = "reponame") val repoName: String,
        @Json(name = "subject") val subject: String?,
        @Json(name = "branch") val branchName: String,
        @Json(name = "build_num") val buildNumber: Int,
        @Json(name = "status") val status: String,
        @Json(name = "user") val user: User,
        @Json(name = "queued_at") val queuedAt: String?
    )
}