package com.nhaarman.bravo.lint

import com.android.tools.lint.checks.infrastructure.TestFiles.kt
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.jupiter.api.Test

class RxSceneDisposablesUsageDetectorTest {

    private val pkg = "com.nhaarman.bravo.presentation"
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