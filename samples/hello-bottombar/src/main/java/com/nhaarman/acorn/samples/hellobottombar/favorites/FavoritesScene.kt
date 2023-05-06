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

package com.nhaarman.acorn.samples.hellobottombar.favorites

import com.nhaarman.acorn.navigation.DisposableHandle
import com.nhaarman.acorn.presentation.BasicScene
import com.nhaarman.acorn.presentation.SavableScene
import com.nhaarman.acorn.presentation.SceneKey
import com.nhaarman.acorn.samples.hellobottombar.DestinationSelectedListener
import com.nhaarman.acorn.state.SceneState

class FavoritesScene(
    private val listener: Events,
    savedState: SceneState? = null,
) : BasicScene<FavoritesContainer>(savedState),
    SavableScene {

    override val key = FavoritesScene.key

    private var listenerDisposable: DisposableHandle? = null
        set(value) {
            field?.dispose()
            field = value
        }

    override fun attach(v: FavoritesContainer) {
        super.attach(v)
        listenerDisposable = v.setDestinationSelectedListener(listener)
    }

    override fun detach(v: FavoritesContainer) {
        listenerDisposable = null
        super.detach(v)
    }

    interface Events : DestinationSelectedListener

    companion object {

        val key = SceneKey.defaultKey<FavoritesScene>()
    }
}
