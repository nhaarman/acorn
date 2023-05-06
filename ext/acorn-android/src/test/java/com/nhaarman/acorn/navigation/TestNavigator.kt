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

package com.nhaarman.acorn.navigation

import com.nhaarman.acorn.presentation.Scene
import org.mockito.kotlin.mock

class TestNavigator : Navigator {

    private var isDestroyed = false

    private var listeners = listOf<Navigator.Events>()

    fun onScene(scene: Scene<*>, data: TransitionData? = null) {
        listeners.forEach { it.scene(scene, data) }
    }

    override fun addNavigatorEventsListener(listener: Navigator.Events): DisposableHandle {
        listeners += listener
        return mock()
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
        isDestroyed = true
    }

    override fun isDestroyed(): Boolean {
        return isDestroyed
    }
}
