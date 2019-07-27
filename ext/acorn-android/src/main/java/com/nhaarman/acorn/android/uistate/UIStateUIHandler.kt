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

package com.nhaarman.acorn.android.uistate

import android.app.Activity
import android.view.ViewGroup
import com.nhaarman.acorn.android.internal.contentView
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.transition.SceneTransitionFactory
import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.util.lazyVar

/**
 * A [UIHandler] that utilizes the [UIState] state machine to handle the UI.
 */
class UIStateUIHandler private constructor(
    private val root: ViewGroup,
    private val transitionFactory: SceneTransitionFactory
) : UIHandler {

    private var state by lazyVar {
        UIState.create(root, transitionFactory)
    }

    override fun onUIVisible() {
        state = state.uiVisible()
    }

    override fun onUINotVisible() {
        state = state.uiNotVisible()
    }

    override fun onBackPressed(): Boolean {
        return state.onBackPressed()
    }

    override fun withScene(
        scene: Scene<out Container>,
        viewControllerFactory: ViewControllerFactory,
        data: TransitionData?
    ) {
        state = state.withScene(scene, viewControllerFactory, data)
    }

    override fun withoutScene() {
        state = state.withoutScene()
    }

    companion object {

        fun create(
            root: ViewGroup,
            transitionFactory: SceneTransitionFactory
        ): UIStateUIHandler {
            return UIStateUIHandler(
                root,
                transitionFactory
            )
        }

        fun create(
            activity: Activity,
            transitionFactory: SceneTransitionFactory
        ): UIStateUIHandler {
            return UIStateUIHandler(
                activity.contentView,
                transitionFactory
            )
        }
    }
}
