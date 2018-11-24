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

package com.nhaarman.acorn.android.dispatching

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.CheckResult
import com.nhaarman.acorn.android.dispatching.internal.ActivityHandler
import com.nhaarman.acorn.android.dispatching.internal.DefaultActivityHandler
import com.nhaarman.acorn.android.internal.contentView
import com.nhaarman.acorn.android.internal.d
import com.nhaarman.acorn.android.presentation.ActivityControllerFactory
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.transition.TransitionFactory
import com.nhaarman.acorn.android.uistate.UIHandler
import com.nhaarman.acorn.android.uistate.UIStateUIHandler
import com.nhaarman.acorn.navigation.DisposableHandle
import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.SavedState
import com.nhaarman.acorn.state.get
import com.nhaarman.acorn.state.savedState

class AcornSceneDispatcher internal constructor(
    private val context: Context,
    private val viewControllerFactory: ViewControllerFactory,
    private val activityControllerFactory: ActivityControllerFactory,
    private val uiHandler: UIHandler,
    private val activityHandler: ActivityHandler,
    private val callback: Callback
) {

    private var listener: MyListener? = null
        set(value) {
            if (field != null) throw IllegalStateException()
            field = value
        }

    @CheckResult
    fun dispatchScenesFor(navigator: Navigator): DisposableHandle {
        val listener = MyListener().also { this.listener = it }
        return navigator.addNavigatorEventsListener(listener)
    }

    fun onUIVisible() {
        uiHandler.onUIVisible()
    }

    fun onUINotVisible() {
        uiHandler.onUINotVisible()
    }

    fun onActivityResult(resultCode: Int, data: Intent?) {
        activityHandler.onActivityResult(resultCode, data)
    }

    fun saveInstanceState(): SavedState {
        return savedState {
            it.activityHandlerState = activityHandler.saveInstanceState()
        }
    }

    private inner class MyListener : Navigator.Events {

        private var lastScene: Scene<*>? = null

        override fun scene(scene: Scene<out Container>, data: TransitionData?) {
            d("ContainerDispatcher", "New active scene: $scene.")

            lastScene = scene

            if (viewControllerFactory.supports(scene.key)) {
                activityHandler.withoutScene()
                uiHandler.withScene(scene, viewControllerFactory, data)
                return
            }

            if (activityControllerFactory.supports(scene.key)) {
                uiHandler.withoutScene()
                activityHandler.withScene(scene, activityControllerFactory.activityControllerFor(scene, context))
                return
            }

            throw IllegalStateException("Could not dispatch $scene.")
        }

        override fun finished() {
            callback.finished()
        }
    }

    interface Callback {

        fun startForResult(intent: Intent)
        fun finished()
    }

    companion object {

        fun create(
            activity: Activity,
            viewControllerFactory: ViewControllerFactory,
            activityControllerFactory: ActivityControllerFactory,
            transitionFactory: TransitionFactory,
            callback: Callback,
            savedState: SavedState?
        ): AcornSceneDispatcher {
            return AcornSceneDispatcher(
                activity,
                viewControllerFactory,
                activityControllerFactory,
                UIStateUIHandler.create(activity.contentView, transitionFactory),
                DefaultActivityHandler(
                    ActivityHandlerCallbackAdapter(callback),
                    savedState.activityHandlerState
                ),
                callback
            )
        }

        private var SavedState?.activityHandlerState: SavedState?
            get() {
                return this?.get("activity_handler")
            }
            set(value) {
                this?.set("activity_handler", value)
            }

        private class ActivityHandlerCallbackAdapter(
            private val dispatcherCallback: Callback
        ) : DefaultActivityHandler.Callback {

            override fun startForResult(intent: Intent) {
                dispatcherCallback.startForResult(intent)
            }
        }
    }
}