package com.nhaarman.bravo.lint

import com.android.tools.lint.checks.infrastructure.TestFiles.kt
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.jupiter.api.Test

class RestorableViewUsageDetectorTest {

    private val restorableViewFQN = "com.nhaarman.bravo.android.presentation.RestorableView"
    private val restorableViewSubFQN = "com.nhaarman.bravo.android.presentation.SubRestorableView"

    private val restorableViewStub = kt(
        """
            |package com.nhaarman.bravo.android.presentation
            |
            |interface RestorableView
        """.trimMargin()
    )

    private val restorableViewSubStub = kt(
        """
            |package com.nhaarman.bravo.android.presentation
            |
            |interface SubRestorableView : RestorableView
        """.trimMargin()
    )

    @Test
    fun `an interface extending RestorableView is allowed`() {
        lint()
            .allowMissingSdk()
            .files(
                restorableViewStub,
                kt(
                    """
                    |interface Foo: $restorableViewFQN
                    """.trimMargin()
                )
            )
            .issues(RestorableViewUsageDetector.issue)
            .run()
            .expectClean()
    }

    @Test
    fun `a class only extending RestorableView is not allowed`() {
        lint()
            .files(
                restorableViewStub,
                kt(
                    """
                    |class Foo: $restorableViewFQN
                    """.trimMargin()
                )
            )
            .issues(RestorableViewUsageDetector.issue)
            .run()
            .expectMatches("Foo implements RestorableView but is not a subclass of View.")
    }

    @Test
    fun `a class extending View is allowed`() {
        lint()
            .files(
                restorableViewStub,
                kt(
                    """
                    |class Foo: android.view.View, $restorableViewFQN
                    """.trimMargin()
                )
            )
            .issues(RestorableViewUsageDetector.issue)
            .run()
            .expectClean()
    }

    @Test
    fun `a class extending LinearLayout is allowed`() {
        lint()
            .files(
                restorableViewStub,
                kt(
                    """
                    |class Foo: android.widget.LinearLayout, $restorableViewFQN
                    """.trimMargin()
                )
            )
            .issues(RestorableViewUsageDetector.issue)
            .run()
            .expectClean()
    }

    @Test
    fun `sub - an interface extending RestorableView is allowed`() {
        lint()
            .allowMissingSdk()
            .files(
                restorableViewSubStub,
                restorableViewStub,
                kt(
                    """
                    |interface Foo: $restorableViewSubFQN
                    """.trimMargin()
                )
            )
            .issues(RestorableViewUsageDetector.issue)
            .run()
            .expectClean()
    }

    @Test
    fun `sub - a class only extending RestorableView is not allowed`() {
        lint()
            .files(
                restorableViewStub,
                restorableViewSubStub,
                kt(
                    """
                    |class Foo: $restorableViewSubFQN
                    """.trimMargin()
                )
            )
            .issues(RestorableViewUsageDetector.issue)
            .run()
            .expectMatches("Foo transitively implements RestorableView but is not a subclass of View.")
    }

    @Test
    fun `sub - a class extending View is allowed`() {
        lint()
            .files(
                restorableViewStub,
                restorableViewSubStub,
                kt(
                    """
                    |class Foo: android.view.View, $restorableViewSubFQN
                    """.trimMargin()
                )
            )
            .issues(RestorableViewUsageDetector.issue)
            .run()
            .expectClean()
    }

    @Test
    fun `sub - a class extending LinearLayout is allowed`() {
        lint()
            .files(
                restorableViewStub,
                restorableViewSubStub,
                kt(
                    """
                    |class Foo: android.widget.LinearLayout, $restorableViewSubFQN
                    """.trimMargin()
                )
            )
            .issues(RestorableViewUsageDetector.issue)
            .run()
            .expectClean()
    }
}