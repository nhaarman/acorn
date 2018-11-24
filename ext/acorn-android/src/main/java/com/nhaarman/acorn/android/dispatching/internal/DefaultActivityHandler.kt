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

package com.nhaarman.acorn.android.dispatching.internal

import android.content.Intent
import com.nhaarman.acorn.android.internal.d
import com.nhaarman.acorn.android.internal.v
import com.nhaarman.acorn.android.internal.w
import com.nhaarman.acorn.android.presentation.ActivityController
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey
import com.nhaarman.acorn.state.SavedState
import com.nhaarman.acorn.state.get
import com.nhaarman.acorn.state.savedState

internal class DefaultActivityHandler(
    private val callback: Callback,
    private val savedState: SavedState?
) : ActivityHandler {

    private var lastScene: Scene<*>? = null
        set(value) {
            field = value
            lastSceneKey = value?.key
        }

    private var lastSceneKey: SceneKey? = savedState.lastSceneKey
    private var lastActivityController: ActivityController? = null

    override fun withScene(scene: Scene<out Container>, activityController: ActivityController) {
        v("ActivityHandler", "Scene changed: $scene.")

        if (lastSceneKey == scene.key) {
            v(
                "ActivityHandler",
                "New external Scene has the same key as the previously dispatched Scene, not starting Activity."
            )
            return
        }

        lastScene = scene
        lastActivityController = activityController

        val intent = activityController.createIntent()

        v("ActivityHandler", "Starting Intent: $intent.")
        callback.startForResult(intent)
    }

    override fun withoutScene() {
        d("ActivityHandler", "Scene lost.")
        lastScene = null
    }

    override fun onActivityResult(resultCode: Int, data: Intent?) {
        d("ActivityHandler", "Activity result: resultCode=$resultCode, data=$data")

        val scene = lastScene
        val activityController = lastActivityController
        if (scene == null || activityController == null) {
            w(
                "ActivityHandler",
                "Activity result without active Scene, dropping result"
            )
            return
        }

        v("ActivityHandler", "Attaching container to $scene.")
        scene.forceAttach(activityController)

        v("ActivityHandler", "Notifying ActivityController of result.")
        activityController.onResult(resultCode, data)

        v("ActivityHandler", "Detaching container from $scene.")
        scene.forceDetach(activityController)
    }

    override fun saveInstanceState(): SavedState {
        return savedState { it.lastSceneKey = lastSceneKey }
    }

    interface Callback {

        fun startForResult(intent: Intent)
    }

    companion object {

        private var SavedState?.lastSceneKey: SceneKey?
            get() = this?.get<String>("scene_key")?.let(::SceneKey)
            set(value) {
                this?.set("scene_key", value?.value)
            }
    }
}

@Suppress("UNCHECKED_CAST")
private fun Scene<*>.forceAttach(c: Container) {
    (this as Scene<Container>).attach(c)
}

@Suppress("UNCHECKED_CAST")
private fun Scene<*>.forceDetach(c: Container) {
    (this as Scene<Container>).detach(c)
}
