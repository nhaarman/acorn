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

import org.jetbrains.annotations.TestOnly
import org.threeten.bp.ZonedDateTime

data class Build(
    val buildNumber: Int,
    val subject: String?,
    val repoName: String,
    val branchName: String,
    val status: Status,
    val queuedAt: ZonedDateTime?,
    val user: User
) {

    enum class Status {
        Scheduled,
        Queued,
        NotRunning,
        Running,
        Success,
        Fixed,
        Failed,
        Canceled
    }
}

@TestOnly
fun build(
    buildNumber: Int = 1,
    subject: String? = null,
    repoName: String = "RepoName",
    branchName: String = "BranchName",
    status: Build.Status = Build.Status.Success,
    queuedAt: ZonedDateTime? = null,
    user: User = user()
) = Build(
    buildNumber = buildNumber,
    subject = subject,
    repoName = repoName,
    branchName = branchName,
    status = status,
    queuedAt = queuedAt,
    user = user
)