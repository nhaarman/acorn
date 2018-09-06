/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

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