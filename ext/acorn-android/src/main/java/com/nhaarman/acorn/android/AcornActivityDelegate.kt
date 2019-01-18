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

package com.nhaarman.acorn.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.nhaarman.acorn.OnBackPressListener
import com.nhaarman.acorn.android.dispatching.AcornSceneDispatcher
import com.nhaarman.acorn.android.internal.contentView
import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.android.presentation.ActivityControllerFactory
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.transition.DefaultTransitionFactory
import com.nhaarman.acorn.android.transition.TransitionFactory
import com.nhaarman.acorn.android.util.toBundle
import com.nhaarman.acorn.android.util.toNavigatorState
import com.nhaarman.acorn.navigation.DisposableHandle
import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.SavedState

class AcornActivityDelegate private constructor(
    private val activity: Activity,
    private val root: ViewGroup,
    private val navigatorProvider: NavigatorProvider,
    private val viewControllerFactory: ViewControllerFactory,
    private val activityControllerFactory: ActivityControllerFactory,
    private val transitionFactory: TransitionFactory
) {

    private lateinit var navigator: Navigator

    /**
     * Returns the navigator used in this instance.
     * Must only be called _after_ [onCreate] has been called.
     */
    fun navigator(): Navigator {
        return navigator
    }

    private lateinit var dispatcher: AcornSceneDispatcher

    private var disposable: DisposableHandle? = null
        set(value) {
            field?.dispose()
            field = value
        }

    @Suppress("UNCHECKED_CAST")
    fun onCreate(savedInstanceState: Bundle?) {
        dispatcher = AcornSceneDispatcher.create(
            activity,
            root,
            viewControllerFactory,
            activityControllerFactory,
            transitionFactory,
            object : AcornSceneDispatcher.Callback {

                override fun startForResult(intent: Intent) {
                    activity.startActivityForResult(intent, 42)
                }

                override fun finished() {
                    activity.finish()
                }
            },
            savedInstanceState.sceneDispatcherState
        )

        navigator = navigatorProvider.navigatorFor(savedInstanceState.navigatorState)
        disposable = dispatcher.dispatchScenesFor(navigator)
    }

    fun onStart() {
        navigator.onStart()
        dispatcher.onUIVisible()
    }

    // We suppress the unused parameter to keep a uniform API.
    @Suppress("UNUSED_PARAMETER")
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        dispatcher.onActivityResult(resultCode, data)
    }

    fun onBackPressed(): Boolean {
        return (navigator as? OnBackPressListener)?.onBackPressed() ?: false
    }

    fun onStop() {
        dispatcher.onUINotVisible()
    }

    fun onDestroy() {
        disposable = null
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.navigatorState = navigatorProvider.saveNavigatorState()
        outState.sceneDispatcherState = dispatcher.saveInstanceState()
    }

    companion object {

        fun from(
            activity: Activity,
            navigatorProvider: NavigatorProvider,
            viewControllerFactory: ViewControllerFactory,
            activityControllerFactory: ActivityControllerFactory,
            transitionFactory: TransitionFactory = DefaultTransitionFactory(viewControllerFactory)
        ): AcornActivityDelegate {
            return from(
                activity,
                activity.contentView,
                navigatorProvider,
                viewControllerFactory,
                activityControllerFactory,
                transitionFactory
            )
        }

        fun from(
            activity: Activity,
            root: ViewGroup,
            navigatorProvider: NavigatorProvider,
            viewControllerFactory: ViewControllerFactory,
            activityControllerFactory: ActivityControllerFactory,
            transitionFactory: TransitionFactory = DefaultTransitionFactory(viewControllerFactory)
        ): AcornActivityDelegate {
            return AcornActivityDelegate(
                activity,
                root,
                navigatorProvider,
                viewControllerFactory,
                activityControllerFactory,
                transitionFactory
            )
        }

        private var Bundle?.navigatorState: NavigatorState?
            get() = this?.getBundle("navigator")?.toNavigatorState()
            set(value) {
                this?.putBundle("navigator", value?.toBundle())
            }

        private var Bundle?.sceneDispatcherState: SavedState?
            get() = this?.getBundle("scene_dispatcher")?.toNavigatorState()
            set(value) {
                this?.putBundle("scene_dispatcher", value?.toBundle())
            }
    }
}