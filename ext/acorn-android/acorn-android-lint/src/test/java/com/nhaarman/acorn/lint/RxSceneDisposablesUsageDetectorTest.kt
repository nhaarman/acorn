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

package com.nhaarman.acorn.lint

import com.android.tools.lint.checks.infrastructure.TestFiles.kt
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.jupiter.api.Test

class RxSceneDisposablesUsageDetectorTest {

    private val pkg = "com.nhaarman.acorn.presentation"
    private val rxScene = kt(
        """
       package $pkg
       class RxScene
    """.trimIndent()
    )

    @Test
    fun `an empty class extending RxScene is allowed`() {
        lint()
            .allowMissingSdk()
            .files(
                rxScene,
                kt(
                    """
                        |class MyScene : $pkg.RxScene()
                    """.trimMargin()
                )
            )
            .issues(RxSceneDisposablesUsageDetector.issue)
            .run()
            .expectClean()
    }

    @Test
    fun `plusAssign in onStart is allowed`() {
        lint()
            .allowMissingSdk()
            .files(
                rxScene,
                kt(
                    """
                        |class MyScene : $pkg.RxScene() {
                        |  override fun onStart() {
                        |    disposables.plusAssign(Observable.just(3).subscribe())
                        |  }
                        |}
                    """.trimMargin()
                )
            )
            .issues(RxSceneDisposablesUsageDetector.issue)
            .run()
            .expectClean()
    }

    @Test
    fun `plusAssign operator in onStart is allowed`() {
        lint()
            .allowMissingSdk()
            .files(
                rxScene,
                kt(
                    """
                        |class MyScene : $pkg.RxScene() {
                        |  override fun onStart() {
                        |    disposables += Observable.just(3).subscribe()
                        |  }
                        |}
                    """.trimMargin()
                )
            )
            .issues(RxSceneDisposablesUsageDetector.issue)
            .run()
            .expectClean()
    }

    @Test
    fun `plusAssign in attach is not allowed`() {
        lint()
            .allowMissingSdk()
            .files(
                rxScene,
                kt(
                    """
                        |class MyScene : $pkg.RxScene() {
                        |  override fun attach(v: View) {
                        |    disposables.plusAssign(3)
                        |  }
                        |}
                    """.trimMargin()
                )
            )
            .issues(RxSceneDisposablesUsageDetector.issue)
            .run()
            .expectMatches("disposables property must only be used from onStart()")
    }

    @Test
    fun `plusAssign operator in attach is not allowed`() {
        lint()
            .allowMissingSdk()
            .files(
                rxScene,
                kt(
                    """
                        |class MyScene : $pkg.RxScene() {
                        |  override fun attach(v: View) {
                        |    disposables += Observable.just(3).subscribe()
                        |  }
                        |}
                    """.trimMargin()
                )
            )
            .issues(RxSceneDisposablesUsageDetector.issue)
            .run()
            .expectMatches("disposables property must only be used from onStart()")
    }

    @Test
    fun `add in attach is not allowed`() {
        lint()
            .allowMissingSdk()
            .files(
                rxScene,
                kt(
                    """
                        |class MyScene : $pkg.RxScene() {
                        |  override fun attach(v: View) {
                        |    disposables.add(3)
                        |  }
                        |}
                    """.trimMargin()
                )
            )
            .issues(RxSceneDisposablesUsageDetector.issue)
            .run()
            .expectMatches("disposables property must only be used from onStart()")
    }
}