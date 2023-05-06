/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
        """.trimIndent(),
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
                    """.trimMargin(),
                ),
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
                    """.trimMargin(),
                ),
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
                    """.trimMargin(),
                ),
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
                    """.trimMargin(),
                ),
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
                    """.trimMargin(),
                ),
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
                    """.trimMargin(),
                ),
            )
            .issues(RxSceneDisposablesUsageDetector.issue)
            .run()
            .expectMatches("disposables property must only be used from onStart()")
    }
}
