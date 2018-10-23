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

    private val navigator = kt(
        """
        package $navigationPkg
        interface Navigator
        """
    )

    private val Navigator = "$navigationPkg.Navigator"

    private val compositeStackNavigator = kt(
        """
       package $navigationPkg
       class CompositeStackNavigator : $Navigator
       """
    )
    private val CompositeStackNavigator = "$navigationPkg.CompositeStackNavigator"

    private val compositeReplacingNavigator = kt(
        """
       package $navigationPkg
       class CompositeReplacingNavigator : Navigator
       """
    )
    private val CompositeReplacingNavigator = "$navigationPkg.CompositeReplacingNavigator"

    private fun runOn(vararg files: TestFile) = lint()
        .files(
            navigator,
            compositeStackNavigator,
            compositeReplacingNavigator,
            *files
        )
        .allowSystemErrors(false)
        .allowCompilationErrors(false)
        .issues(RestoreNavigatorDetector.issue)
        .run()

    @Test
    fun `an empty stack navigator implementation is allowed`() {
        runOn(
            kt(
                """
                class MyNavigator: $CompositeStackNavigator()
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
                class MyCompositeNavigator: $CompositeStackNavigator() {

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
                    class MyNavigator : $Navigator
                """
            ),
            kt(
                """
                class MyOtherClass {

                    fun foo() {
                       val a = MyNavigator()
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
                class MyNavigator : $Navigator
                """
            ),
            kt(
                """
                class MyCompositeNavigator : $CompositeStackNavigator() {

                    fun foo() {
                       val a = MyNavigator()
                    }

                    override fun instantiateNavigator(navigatorClass: Class<$Navigator>, savedState: NavigatorState) : $Navigator {
                        return MyNavigator()
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
                class MyNavigator : $Navigator {

                  companion object {

                    fun create() : MyNavigator = MyNavigator()
                  }
                }
                """
            ),
            kt(
                """
                class MyCompositeNavigator : $CompositeStackNavigator() {

                    fun foo() {
                       val a = MyNavigator()
                    }

                    override fun instantiateNavigator(navigatorClass: Class<$Navigator>, savedState: NavigatorState) : $Navigator {
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
                    class MyNavigator : $Navigator
            """
            ),
            kt(
                """
                class MyCompositeNavigator : $CompositeStackNavigator() {

                    fun foo() {
                       val a = MyNavigator()
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
                    class MyNavigator : $Navigator {

                        companion object {

                            fun create(): MyNavigator {
                                return MyNavigator()
                            }
                        }
                    }
            """
            ),
            kt(
                """
                class MyCompositeNavigator : $CompositeStackNavigator() {

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
                    class MyNavigator : $Navigator
            """
            ),
            kt(
                """
                    class MyNavigator2 : $Navigator
            """
            ),
            kt(
                """
                class MyCompositeNavigator : $CompositeStackNavigator() {

                    fun foo() {
                       val a = MyNavigator()
                       val b = MyNavigator2()
                    }

                    override fun instantiateNavigator(navigatorClass: Class<$Navigator>, savedState: NavigatorState) : $Navigator {
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
                    class MyNavigator : $Navigator
            """
            ),
            kt(
                """
                    class MyNavigator2 : $Navigator
            """
            ),
            kt(
                """
                class MyCompositeNavigator : $CompositeReplacingNavigator {

                    fun foo() {
                       val a = MyNavigator()
                       val b = MyNavigator2()
                    }

                    override fun instantiateNavigator(navigatorClass: Class<$Navigator>, savedState: NavigatorState) : $Navigator {
                       return MyNavigator.create()
                    }
                }
                """
            )
        ).expectMatches("Navigator is not restored")
    }

    @Test
    fun `creating but not restoring a Navigator in a function with a Navigator return type does not warn`() {
        runOn(
            kt(
                """
                class MyNavigator : $Navigator
                """
            ),
            kt(
                """
                class MyCompositeNavigator : $CompositeStackNavigator() {

                    fun foo() {
                       val a = create()
                    }

                    fun create() : $Navigator {
                       return MyNavigator()
                    }

                    override fun instantiateNavigator(navigatorClass: Class<$Navigator>, savedState: NavigatorState) : $Navigator {
                        return MyNavigator()
                    }
                }
                """
            )
        ).expectClean()
    }
}