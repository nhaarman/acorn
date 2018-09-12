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

package com.nhaarman.bravo.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.nhaarman.bravo.OnBackPressListener
import com.nhaarman.bravo.android.internal.v
import com.nhaarman.bravo.android.internal.w
import com.nhaarman.bravo.android.navigation.NavigatorProvider
import com.nhaarman.bravo.android.presentation.IntentProvider
import com.nhaarman.bravo.android.presentation.NoIntentProvider
import com.nhaarman.bravo.android.presentation.ViewFactory
import com.nhaarman.bravo.android.presentation.internal.DefaultSceneTransformer
import com.nhaarman.bravo.android.presentation.internal.SceneTransformer
import com.nhaarman.bravo.android.presentation.internal.TransformedScene
import com.nhaarman.bravo.android.presentation.internal.ViewState
import com.nhaarman.bravo.android.transition.DefaultTransitionFactory
import com.nhaarman.bravo.android.transition.TransitionFactory
import com.nhaarman.bravo.android.util.toBundle
import com.nhaarman.bravo.android.util.toNavigatorState
import com.nhaarman.bravo.navigation.DisposableHandle
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.navigation.TransitionData
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.util.lazyVar

class BravoActivityDelegate private constructor(
    private val activity: Activity,
    private val navigatorProvider: NavigatorProvider,
    private val viewFactory: ViewFactory,
    private val transitionFactory: TransitionFactory,
    private val intentProvider: IntentProvider,
    private val sceneTransformer: SceneTransformer = DefaultSceneTransformer(intentProvider)
) {

    private lateinit var navigator: Navigator<Navigator.Events>

    private var state by lazyVar {
        ViewState.create(activity.root, viewFactory, transitionFactory)
    }

    private val sceneDispatcher = SceneDispatcher()

    /**
     * To prevent starting the external activity again when it returns after a
     * process death, we store the class name of the last Scene that was external.
     * This value is `null` if the last Scene was not external.
     * This property is saved and restored across process deaths.
     */
    private var lastExternalSceneClass: String? = null

    private var disposable: DisposableHandle? = null
        set(value) {
            field?.dispose()
            field = value
        }

    @Suppress("UNCHECKED_CAST")
    fun onCreate(savedInstanceState: Bundle?) {
        lastExternalSceneClass = savedInstanceState.lastExternalSceneClass

        navigator = navigatorProvider.navigatorFor(savedInstanceState.navigatorState) as Navigator<Navigator.Events>

        disposable = navigator.addListener(sceneDispatcher)
    }

    fun onStart() {
        navigator.onStart()
        state = state.started()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        sceneDispatcher.onActivityResult(requestCode, resultCode, data)
    }

    fun onBackPressed(): Boolean {
        return (navigator as? OnBackPressListener)?.onBackPressed() ?: false
    }

    fun onStop() {
        state = state.stopped()
    }

    fun onDestroy() {
        disposable = null
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.navigatorState = navigatorProvider.saveNavigatorState()
        outState.lastExternalSceneClass = lastExternalSceneClass
    }

    private inner class SceneDispatcher : Navigator.Events {

        private var lastScene: Scene<*>? = null

        override fun scene(scene: Scene<out Container>, data: TransitionData?) {
            lastScene = scene

            val transformedScene = sceneTransformer.transform(scene, data)
            when (transformedScene) {
                is TransformedScene.ContainerScene -> dispatchContainerScene(transformedScene)
                is TransformedScene.ExternalScene -> dispatchExternalScene(transformedScene)
            }
        }

        private fun dispatchContainerScene(scene: TransformedScene.ContainerScene) {
            lastExternalSceneClass = null
            state = state.withScene(scene.scene, scene.data)
        }

        private fun dispatchExternalScene(scene: TransformedScene.ExternalScene) {
            if (lastExternalSceneClass == scene.javaClass.name) {
                v(
                    "BravoActivityDelegate",
                    "New external Scene has the same class as the previously dispatched Scene, not starting Activity."
                )
                return
            }

            lastExternalSceneClass = scene.javaClass.name
            state = state.withoutScene()
            activity.startActivityForResult(scene.intent, 42)
        }

        @Suppress("UNUSED_PARAMETER")
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            val scene = lastScene
            if (scene == null) {
                w("BravoActivityDelegate", "Activity result without active Scene, dropping result")
                return
            }

            intentProvider.onActivityResult(scene, resultCode, data)
        }

        override fun finished() {
            activity.finish()
        }
    }

    companion object {

        fun from(
            activity: Activity,
            navigatorProvider: NavigatorProvider,
            viewFactory: ViewFactory,
            transitionFactory: TransitionFactory = DefaultTransitionFactory(viewFactory),
            intentProvider: IntentProvider = NoIntentProvider
        ): BravoActivityDelegate {
            return BravoActivityDelegate(
                activity,
                navigatorProvider,
                viewFactory,
                transitionFactory,
                intentProvider
            )
        }

        private var Bundle?.navigatorState: NavigatorState?
            get() = this?.getBundle("navigator")?.toNavigatorState()
            set(value) {
                this?.putBundle("navigator", value?.toBundle())
            }

        private var Bundle?.lastExternalSceneClass: String?
            get() = this?.getString("last_external_scene", null)
            set(value) {
                this?.putString("last_external_scene", value)
            }

        private val Activity.root get() = findViewById<ViewGroup>(android.R.id.content)
    }
}