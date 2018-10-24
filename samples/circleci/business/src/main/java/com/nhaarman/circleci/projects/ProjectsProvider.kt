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

import com.nhaarman.circleci.Project
import com.nhaarman.httpmonads.HttpTry
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface ProjectsProvider {

    val projects: Observable<HttpTry<List<Project>>>

    fun refresh()

    companion object Factory {

        fun createDummyProjectsProvider(): ProjectsProvider {
            return DummyProjectsProvider()
        }

        fun createWebProjectsProvider(projectsService: ProjectsService): ProjectsProvider {
            return WebProjectsProvider(projectsService)
        }
    }
}

internal class DummyProjectsProvider : ProjectsProvider {

    private val refreshSubject = PublishSubject.create<Unit>()

    override val projects: Observable<HttpTry<List<Project>>> by lazy {
        refreshSubject.startWith(Unit)
            .switchMap {
                Observable.just(HttpTry.success(emptyList<Project>()))
            }
    }

    override fun refresh() {
        refreshSubject.onNext(Unit)
    }
}

internal class WebProjectsProvider(
    private val projectsService: ProjectsService
) : ProjectsProvider {

    private val refreshSubject = PublishSubject.create<Unit>()

    override val projects: Observable<HttpTry<List<Project>>> by lazy {
        refreshSubject.startWith(Unit)
            .flatMapSingle { projectsService.retrieveProjects() }
            .replay(1).refCount()
    }

    override fun refresh() {
        refreshSubject.onNext(Unit)
    }
}