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

import com.nhaarman.acorn.navigation.CompositeDisposableHandle
import com.nhaarman.acorn.presentation.BasicScene
import com.nhaarman.acorn.presentation.SavableScene
import com.nhaarman.acorn.presentation.SceneKey
import com.nhaarman.acorn.samples.hellobottombar.DestinationSelectedListener
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.state.get

class MusicScene(
    private val value: Int,
    private val listener: Events,
    savedState: SceneState? = null
) : BasicScene<MusicContainer>(savedState),
    SavableScene {

    override val key = MusicScene.key

    private val disposables = CompositeDisposableHandle()

    override fun attach(v: MusicContainer) {
        super.attach(v)

        v.value = value

        disposables += v.setButtonListener { listener.onButtonTapped(value) }
        disposables += v.setDestinationSelectedListener(listener)
    }

    override fun saveInstanceState(): SceneState {
        return super.saveInstanceState()
            .also { it["value"] = value }
    }

    override fun detach(v: MusicContainer) {
        disposables.clear()
        super.detach(v)
    }

    interface Events : DestinationSelectedListener {

        fun onButtonTapped(value: Int)
    }

    companion object {

        val key = SceneKey.defaultKey<MusicScene>()

        fun from(
            listener: Events,
            savedState: SceneState
        ): MusicScene {
            return MusicScene(
                savedState["value"]!!,
                listener,
                savedState
            )
        }
    }
}
