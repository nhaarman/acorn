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

package com.nhaarman.acorn.samples.hellobottombar.music

import com.nhaarman.acorn.navigation.SavableNavigator
import com.nhaarman.acorn.navigation.StackNavigator
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.samples.hellobottombar.DestinationSelectedListener
import com.nhaarman.acorn.samples.hellobottombar.MyDestination
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.SceneState
import kotlin.reflect.KClass

class MusicNavigator(
    private val listener: Events,
    savedState: NavigatorState? = null,
) : StackNavigator(savedState),
    SavableNavigator {

    override fun initialStack(): List<Scene<out Container>> {
        return listOf(MusicScene(1, MusicSceneListener()))
    }

    override fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<out Container> {
        return MusicScene.from(MusicSceneListener(), state!!)
    }

    private inner class MusicSceneListener : MusicScene.Events {

        override fun onButtonTapped(value: Int) {
            push(MusicScene(value + 1, MusicSceneListener()))
        }

        override fun onDestinationSelected(destination: MyDestination) {
            listener.onDestinationSelected(destination)
        }
    }

    interface Events : DestinationSelectedListener
}
