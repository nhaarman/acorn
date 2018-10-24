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

package com.nhaarman.circleci.projects

import com.nhaarman.circleci.Branch
import com.nhaarman.circleci.Project
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

internal object ProjectAdapter {

    @FromJson
    fun fromJson(json: ProjectJson): Project {
        return Project(
            repoName = json.repoName,
            branches = json.branches.map { (key, value) ->
                branchFrom(key, value)
            }
        )
    }

    private fun branchFrom(name: String, json: BranchJson): Branch {
        return Branch(
            name = name
        )
    }

    @ToJson
    fun toJson(project: Project): String = error("Not supported")

    @JsonClass(generateAdapter = true)
    internal class ProjectJson(
        @Json(name = "reponame") val repoName: String,
        @Json(name = "branches") val branches: Map<String, BranchJson>
    )

    @JsonClass(generateAdapter = true)
    internal class BranchJson
}