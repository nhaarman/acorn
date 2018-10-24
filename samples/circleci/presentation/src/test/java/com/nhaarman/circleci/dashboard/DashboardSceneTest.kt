package com.nhaarman.circleci.dashboard

import arrow.core.Either
import com.nhaarman.acorn.testing.RestorableTestContainer
import com.nhaarman.circleci.Build
import com.nhaarman.circleci.build
import com.nhaarman.circleci.builds.CacherError
import com.nhaarman.circleci.builds.RecentBuildsProvider
import com.nhaarman.expect.expect
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(TrampolineMainThreadExtension::class)
internal class DashboardSceneTest {

    private val recentBuildsProvider = TestRecentBuildsProvider()
    private val dashboardScene = DashboardScene(recentBuildsProvider)
    private val container = TestDashboardContainer()

    @Test
    fun `attaching container propagates view model`() {
        /* Given */
        dashboardScene.onStart()

        val builds = listOf(build(buildNumber = 3))
        recentBuildsProvider.buildsList = builds

        /* When */
        dashboardScene.attach(container)

        /* Then */
        expect(container.recentBuilds).toBe(Either.right(builds))
    }

    @Test
    fun `updated view model is propagated`() {
        /* Given */
        dashboardScene.onStart()
        dashboardScene.attach(container)

        val builds = listOf(build(buildNumber = 3))

        /* When */
        recentBuildsProvider.buildsList = builds

        /* Then */
        expect(container.recentBuilds).toBe(Either.right(builds))
    }

    @Test
    fun `attaching new container`() {
        /* Given */
        dashboardScene.onStart()
        dashboardScene.attach(container)

        val builds = listOf(build(buildNumber = 3))
        recentBuildsProvider.buildsList = builds

        val newContainer = TestDashboardContainer()

        /* When */
        dashboardScene.detach(container)
        dashboardScene.attach(newContainer)

        /* Then */
        expect(newContainer.recentBuilds).toBe(Either.right(builds))
    }

    private class TestDashboardContainer : DashboardContainer, RestorableTestContainer {

        override var recentBuilds: Either<CacherError<List<Build>>, List<Build>>? = null

        override val refreshRequests = PublishSubject.create<Unit>()
    }

    private class TestRecentBuildsProvider : RecentBuildsProvider {

        var buildsList: List<Build>? = null
            set(value) {
                builds.onNext(Either.right(value!!))
            }

        override val builds =
            BehaviorSubject.create<Either<CacherError<List<Build>>, List<Build>>>()

        override fun refresh() {
        }
    }
}