package com.nhaarman.bravo.lint

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.kt
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.jupiter.api.Test

class RestoreSceneDetectorTest {

    private val navigationPkg = "com.nhaarman.bravo.navigation"
    private val stackNavigator = kt(
        """
       package $navigationPkg
       class StackNavigator<T>
       """
    )

    private val replacingNavigator = kt(
        """
       package $navigationPkg
       class ReplacingNavigator<T>
       """
    )
    private val wizardNavigator = kt(
        """
       package $navigationPkg
       class WizardNavigator<T>
       """
    )

    private val presentationPkg = "com.nhaarman.bravo.presentation"
    private val scene = kt(
        """
        package $presentationPkg
        interface Scene
        """
    )

    private fun runOn(vararg files: TestFile) = lint()
        .allowMissingSdk()
        .files(
            stackNavigator,
            replacingNavigator,
            wizardNavigator,
            scene,
            *files
        )
        .issues(RestoreSceneDetector.issue)
        .run()

    @Test
    fun `an empty stack navigator implementation is allowed`() {
        runOn(
            kt(
                """
                import $navigationPkg.StackNavigator
                class MyScene: StackNavigator<Unit>()
                """
            )
        ).expectClean()
    }

    @Test
    fun `creating a non Scene class is allowed`() {
        runOn(
            kt("class MyNonScene"),
            kt(
                """
                import $navigationPkg.StackNavigator
                class MyNavigator: StackNavigator<Unit>() {

                    fun foo() {
                       val a = MyNonScene()
                    }
                }
                """
            )
        ).expectClean()
    }

    @Test
    fun `creating a Scene class in a normal class is allowed`() {
        runOn(
            kt(
                """
                    import $presentationPkg.Scene
                    class MyScene : Scene
                """
            ),
            kt(
                """
                class MyNavigator {

                    fun foo() {
                       val a = MyScene()
                    }
                }
                """
            )
        ).expectClean()
    }

    @Test
    fun `creating and restoring a Scene class is allowed`() {
        runOn(
            kt(
                """
                import $presentationPkg.Scene
                class MyScene : Scene
                """
            ),
            kt(
                """
                import $navigationPkg.StackNavigator
                class MyNavigator : StackNavigator<Unit> {

                    fun foo() {
                       val a = MyScene()
                    }

                    override fun instantiateScene(sceneClass: Class<Scene<*>>, savedState: SceneState<*>) : Scene<*> {
                        return MyScene()
                    }
                }
                """
            )
        ).expectClean()
    }

    @Test
    fun `creating and restoring a Scene class through companion object is allowed`() {
        runOn(
            kt(
                """
                import $presentationPkg.Scene
                class MyScene : Scene {

                  companion object {

                    fun create() : MyScene = MyScene()
                  }
                }
                """
            ),
            kt(
                """
                import $navigationPkg.StackNavigator
                class MyNavigator : StackNavigator<Unit> {

                    fun foo() {
                       val a = MyScene()
                    }

                    override fun instantiateScene(sceneClass: Class<Scene<*>>, savedState: SceneState<*>) : Scene<*> {
                        return MyScene.create()
                    }
                }
                """
            )
        ).expectClean()
    }

    @Test
    fun `creating but not restoring a Scene class in a StackNavigator gives warning`() {
        runOn(
            kt(
                """
                    import $presentationPkg.Scene
                    class MyScene : Scene
            """
            ),
            kt(
                """
                import $navigationPkg.StackNavigator
                class MyNavigator : StackNavigator<Unit> {

                    fun foo() {
                       val a = MyScene()
                    }
                }
                """
            )
        ).expectMatches("Scene is not restored")
    }

    @Test
    fun `creating but not restoring a Scene class in a ReplacingNavigator gives warning`() {
        runOn(
            kt(
                """
                    import $presentationPkg.Scene
                    class MyScene : Scene
            """
            ),
            kt(
                """
                import $navigationPkg.ReplacingNavigator
                class MyNavigator : ReplacingNavigator<Unit> {

                    fun foo() {
                       val a = MyScene()
                    }
                }
                """
            )
        ).expectMatches("Scene is not restored")
    }

    @Test
    fun `creating but not restoring a Scene class in a WizardNavigator gives warning`() {
        runOn(
            kt(
                """
                    import $presentationPkg.Scene
                    class MyScene : Scene
            """
            ),
            kt(
                """
                import $navigationPkg.WizardNavigator
                class MyNavigator : WizardNavigator<Unit> {

                    fun foo() {
                       val a = MyScene()
                    }
                }
                """
            )
        ).expectMatches("Scene is not restored")
    }

    @Test
    fun `forgetting to restore a Scene class in a StackNavigator gives warning`() {
        runOn(
            kt(
                """
                    import $presentationPkg.Scene
                    class MyScene : Scene
            """
            ),
            kt(
                """
                    import $presentationPkg.Scene
                    class MyScene2 : Scene
            """
            ),
            kt(
                """
                import $navigationPkg.StackNavigator
                class MyNavigator : StackNavigator<Unit> {

                    fun foo() {
                       val a = MyScene()
                       val b = MyScene2()
                    }

                    override fun instantiateScene(sceneClass: Class<Scene<*>>, savedState: SceneState<*>) : Scene<*> {
                       return MyScene.create()
                    }
                }
                """
            )
        ).expectMatches("Scene is not restored")
    }
}