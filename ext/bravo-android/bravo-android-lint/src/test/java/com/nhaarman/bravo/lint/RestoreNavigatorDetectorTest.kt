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
            stackNavigator,
            replacingNavigator,
            wizardNavigator,
            navigator,
            *files
        )
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
                class MyNavigator: CompositeStackNavigator<Unit>() {

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
                    class MyNavigator : Navigator
                """
            ),
            kt(
                """
                class MyNavigator {

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
                import $navigationPkg.Navigator
                class MyNavigator : Navigator
                """
            ),
            kt(
                """
                import $navigationPkg.CompositeStackNavigator
                class MyNavigator : CompositeStackNavigator<Unit> {

                    fun foo() {
                       val a = MyNavigator()
                    }

                    override fun instantiateNavigator(navigatorClass: Class<Navigator<*>>, savedState: NavigatorState<*>) : Navigator<*> {
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
                import $navigationPkg.Navigator
                class MyNavigator : Navigator {

                  companion object {

                    fun create() : MyNavigator = MyNavigator()
                  }
                }
                """
            ),
            kt(
                """
                import $navigationPkg.CompositeStackNavigator
                class MyCompositeNavigator : CompositeStackNavigator<Unit> {

                    fun foo() {
                       val a = MyNavigator()
                    }

                    override fun instantiateNavigator(navigatorClass: Class<Navigator<*>>, savedState: NavigatorState<*>) : Navigator<*> {
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
                    class MyNavigator : Navigator<T>
            """
            ),
            kt(
                """
                import $navigationPkg.CompositeStackNavigator
                class MyNavigator : CompositeStackNavigator<Unit> {

                    fun foo() {
                       val a = MyNavigator()
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
                    class MyNavigator : Navigator
            """
            ),
            kt(
                """
                    import $navigationPkg.Navigator
                    class MyNavigator2 : Navigator
            """
            ),
            kt(
                """
                import $navigationPkg.CompositeStackNavigator
                class MyNavigator : CompositeStackNavigator<Unit> {

                    fun foo() {
                       val a = MyNavigator()
                       val b = MyNavigator2()
                    }

                    override fun instantiateNavigator(navigatorClass: Class<Navigator<*>>, savedState: NavigatorState<*>) : Navigator<*> {
                       return MyNavigator.create()
                    }
                }
                """
            )
        ).expectMatches("Navigator is not restored")
    }
}