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

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.kt
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.jupiter.api.Test

class RestoreNavigatorDetectorTest {

    private val navigationPkg = "com.nhaarman.bravo.navigation"

    private val compositeStackNavigator = kt(
        """
       package $navigationPkg
       class CompositeStackNavigator<T> : Navigator<T>
       """
    )
    private val compositeReplacingNavigator = kt(
        """
       package $navigationPkg
       class CompositeReplacingNavigator<T> : Navigator<T>
       """
    )
    private val stackNavigator = kt(
        """
       package $navigationPkg
       class StackNavigator<T> : Navigator<T>
       """
    )

    private val replacingNavigator = kt(
        """
       package $navigationPkg
       class ReplacingNavigator<T> : Navigator<T>
       """
    )
    private val wizardNavigator = kt(
        """
       package $navigationPkg
       class WizardNavigator<T> : Navigator<T>
       """
    )

    private val navigator = kt(
        """
        package $navigationPkg
        interface Navigator<T>
        """
    )

    private fun runOn(vararg files: TestFile) = lint()
        .allowMissingSdk()
        .files(
            compositeStackNavigator,
            compositeReplacingNavigator,
            stackNavigator,
            replacingNavigator,
            wizardNavigator,
            navigator,
            *files
        )
        .allowCompilationErrors(false)
        .issues(RestoreNavigatorDetector.issue)
        .run()

    @Test
    fun `an empty stack navigator implementation is allowed`() {
        runOn(
            kt(
                """
                import $navigationPkg.CompositeStackNavigator
                class MyNavigator: CompositeStackNavigator<Unit>()
                """
            )
        ).expectClean()
    }

    @Test
    fun `creating a non Navigator class is allowed`() {
        runOn(
            kt("class MyNonNavigator"),
            kt(
                """
                import $navigationPkg.CompositeStackNavigator
                class MyCompositeNavigator: CompositeStackNavigator<Unit>() {

                    fun foo() {
                       val a = MyNonNavigator()
                    }
                }
                """
            )
        ).expectClean()
    }

    @Test
    fun `creating a Navigator class in a normal class is allowed`() {
        runOn(
            kt(
                """
                    import $navigationPkg.Navigator
                    class MyNavigator<T> : Navigator<T>
                """
            ),
            kt(
                """
                class MyOtherClass {

                    fun foo() {
                       val a = MyNavigator<Unit>()
                    }
                }
                """
            )
        ).expectClean()
    }

    @Test
    fun `creating and restoring a Navigator class is allowed`() {
        runOn(
            kt(
                """
                import $navigationPkg.Navigator
                class MyNavigator<T> : Navigator<T>
                """
            ),
            kt(
                """
                import $navigationPkg.CompositeStackNavigator
                class MyCompositeNavigator : CompositeStackNavigator<Unit>() {

                    fun foo() {
                       val a = MyNavigator<Unit>()
                    }

                    override fun instantiateNavigator(navigatorClass: Class<Navigator<*>>, savedState: NavigatorState<*>) : Navigator<T> {
                        return MyNavigator<Unit>()
                    }
                }
                """
            )
        ).expectClean()
    }

    @Test
    fun `creating and restoring a Navigator class through companion object is allowed`() {
        runOn(
            kt(
                """
                import $navigationPkg.Navigator
                class MyNavigator<T> : Navigator<T> {

                  companion object {

                    fun create() : MyNavigator<Unit> = MyNavigator<Unit>()
                  }
                }
                """
            ),
            kt(
                """
                import $navigationPkg.CompositeStackNavigator
                class MyCompositeNavigator : CompositeStackNavigator<Unit>() {

                    fun foo() {
                       val a = MyNavigator<Unit>()
                    }

                    override fun instantiateNavigator(navigatorClass: Class<Navigator<*>>, savedState: NavigatorState<*>) : Navigator<T> {
                        return MyNavigator.create()
                    }
                }
                """
            )
        ).expectClean()
    }

    @Test
    fun `creating but not restoring a Navigator class in a CompositeStackNavigator gives warning`() {
        runOn(
            kt(
                """
                    import $navigationPkg.Navigator
                    class MyNavigator<T> : Navigator<T>
            """
            ),
            kt(
                """
                import $navigationPkg.CompositeStackNavigator
                class MyCompositeNavigator : CompositeStackNavigator<Unit>() {

                    fun foo() {
                       val a = MyNavigator<Unit>()
                    }
                }
                """
            )
        ).expectMatches("Navigator is not restored")
    }

    @Test
    fun `creating but not restoring a Navigator class through non-constructor in a CompositeStackNavigator gives warning`() {
        runOn(
            kt(
                """
                    import $navigationPkg.Navigator
                    class MyNavigator<T> : Navigator<T> {

                        companion object {

                            fun create(): MyNavigator<Unit> {
                                return MyNavigator<Unit>()
                            }
                        }
                    }
            """
            ),
            kt(
                """
                import $navigationPkg.CompositeStackNavigator
                class MyCompositeNavigator : CompositeStackNavigator<Unit>() {

                    fun foo() {
                       val a = MyNavigator.create()
                    }
                }
                """
            )
        ).expectMatches("Navigator is not restored")
    }

    @Test
    fun `forgetting to restore a Navigator class in a CompositeStackNavigator gives warning`() {
        runOn(
            kt(
                """
                    import $navigationPkg.Navigator
                    class MyNavigator<T> : Navigator<T>
            """
            ),
            kt(
                """
                    import $navigationPkg.Navigator
                    class MyNavigator2 : Navigator<T>
            """
            ),
            kt(
                """
                import $navigationPkg.CompositeStackNavigator
                class MyCompositeNavigator : CompositeStackNavigator<Unit>() {

                    fun foo() {
                       val a = MyNavigator<Unit>()
                       val b = MyNavigator2()
                    }

                    override fun instantiateNavigator(navigatorClass: Class<Navigator<*>>, savedState: NavigatorState<*>) : Navigator<T> {
                       return MyNavigator.create()
                    }
                }
                """
            )
        ).expectMatches("Navigator is not restored")
    }

    @Test
    fun `forgetting to restore a Navigator class in a CompositeReplacingNavigator gives warning`() {
        runOn(
            kt(
                """
                    import $navigationPkg.Navigator
                    class MyNavigator<T> : Navigator<T>
            """
            ),
            kt(
                """
                    import $navigationPkg.Navigator
                    class MyNavigator2 : Navigator<T>
            """
            ),
            kt(
                """
                import $navigationPkg.CompositeReplacingNavigator
                class MyCompositeNavigator : CompositeReplacingNavigator<Unit> {

                    fun foo() {
                       val a = MyNavigator<Unit>()
                       val b = MyNavigator2()
                    }

                    override fun instantiateNavigator(navigatorClass: Class<Navigator<*>>, savedState: NavigatorState<*>) : Navigator<T> {
                       return MyNavigator.create()
                    }
                }
                """
            )
        ).expectMatches("Navigator is not restored")
    }
}