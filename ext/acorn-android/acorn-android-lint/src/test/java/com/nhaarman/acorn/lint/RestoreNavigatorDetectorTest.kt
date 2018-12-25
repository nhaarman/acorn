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

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.kt
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.jupiter.api.Test

class RestoreNavigatorDetectorTest {

    private val navigationPkg = "com.nhaarman.acorn.navigation"

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