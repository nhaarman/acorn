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

package com.nhaarman.acorn.graphgen.android

import com.nhaarman.acorn.android.experimental.AcornEvents
import com.nhaarman.acorn.android.experimental.ExperimentalAcornEvents
import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene

@UseExperimental(ExperimentalAcornEvents::class)
internal class GraphGenHook {

    private val listener = MyDispatchingListener()

    fun start() {
        AcornEvents.registerDispatchingListener(listener)
    }

    private class MyDispatchingListener : AcornEvents.DispatchingListener {

        private val graph = Graph()

        override fun onStartDispatching(navigatorProvider: NavigatorProvider, instance: Navigator) {
            Server(graph).start()
            instance.addNavigatorEventsListener(object : Navigator.Events {
                override fun scene(scene: Scene<out Container>, data: TransitionData?) {
                    graph.to(Node(scene.key.value))
                }

                override fun finished() {
                }
            })
        }

        override fun onStopDispatching(navigatorProvider: NavigatorProvider, instance: Navigator) {
        }
    }
}

