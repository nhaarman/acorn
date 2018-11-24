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

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.kt
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.jupiter.api.Test

class RestoreSceneDetectorTest {

    private val navigationPkg = "com.nhaarman.acorn.navigation"
    private val stackNavigator = kt(
        """
       package $navigationPkg
       class StackNavigator
       """
    )

    private val replacingNavigator = kt(
        """
       package $navigationPkg
       class ReplacingNavigator
       """
    )
    private val wizardNavigator = kt(
        """
       package $navigationPkg
       class WizardNavigator
       """
    )

    private val presentationPkg = "com.nhaarman.acorn.presentation"
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
                class MyNavigator: StackNavigator()
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
                class MyNavigator: StackNavigator() {

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
                class MyNavigator : StackNavigator {

                    fun foo() {
                       val a = MyScene()
                    }

                    override fun instantiateScene(sceneClass: KClass<out Scene>, savedState: SceneState) : Scene {
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
                class MyNavigator : StackNavigator {

                    fun foo() {
                       val a = MyScene()
                    }

                    override fun instantiateScene(sceneClass: KClass<out Scene>, savedState: SceneState) : Scene {
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
                class MyNavigator : StackNavigator {

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
                class MyNavigator : ReplacingNavigator {

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
                class MyNavigator : WizardNavigator {

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
                class MyNavigator : StackNavigator {

                    fun foo() {
                       val a = MyScene()
                       val b = MyScene2()
                    }

                    override fun instantiateScene(sceneClass: KClass<out Scene>, savedState: SceneState) : Scene {
                       return MyScene()
                    }
                }
                """
            )
        ).expectMatches("Scene is not restored")
    }

    @Test
    fun `forgetting to restore a Scene class created through non-constructor in a StackNavigator gives warning`() {
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
                    class MyScene2 : Scene {

                        companion object {

                            fun create() = MyScene2()
                        }
                    }
            """
            ),
            kt(
                """
                import $navigationPkg.StackNavigator
                class MyNavigator : StackNavigator {

                    fun foo() {
                       val a = MyScene()
                       val b = MyScene2.create()
                    }

                    override fun instantiateScene(sceneClass: KClass<out Scene>, savedState: SceneState) : Scene {
                       return MyScene()
                    }
                }
                """
            )
        ).expectMatches("Scene is not restored")
    }

    @Test
    fun `creating but not restoring a Scene class in a function with a Scene return type does not warn`() {
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
                import $navigationPkg.StackNavigator
                class MyNavigator : StackNavigator {

                    fun foo() {
                       val a = create()
                    }

                    fun create(): Scene {
                       return MyScene()
                    }

                    override fun instantiateScene(sceneClass: KClass<out Scene>, savedState: SceneState) : Scene {
                       return MyScene()
                    }
                }
                """
            )
        ).expectClean()
    }
}
