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
import com.nhaarman.bravo.OnBackPressListener
import com.nhaarman.bravo.android.dispatching.BravoSceneDispatcher
import com.nhaarman.bravo.android.navigation.NavigatorProvider
import com.nhaarman.bravo.android.presentation.ActivityControllerFactory
import com.nhaarman.bravo.android.presentation.ViewControllerFactory
import com.nhaarman.bravo.android.transition.DefaultTransitionFactory
import com.nhaarman.bravo.android.transition.TransitionFactory
import com.nhaarman.bravo.android.util.toBundle
import com.nhaarman.bravo.android.util.toNavigatorState
import com.nhaarman.bravo.navigation.DisposableHandle
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.state.SavedState

class BravoActivityDelegate private constructor(
    private val activity: Activity,
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

    private lateinit var dispatcher: BravoSceneDispatcher

    private var disposable: DisposableHandle? = null
        set(value) {
            field?.dispose()
            field = value
        }

    @Suppress("UNCHECKED_CAST")
    fun onCreate(savedInstanceState: Bundle?) {
        dispatcher = BravoSceneDispatcher.create(
            activity,
            viewControllerFactory,
            activityControllerFactory,
            transitionFactory,
            object : BravoSceneDispatcher.Callback {

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
        ): BravoActivityDelegate {
            return BravoActivityDelegate(
                activity,
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