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

package com.nhaarman.acorn.notesapp.presentation.itemlist

import com.nhaarman.acorn.notesapp.mainThread
import com.nhaarman.acorn.notesapp.note.NoteItem
import com.nhaarman.acorn.notesapp.note.NoteItemsRepository
import com.nhaarman.acorn.presentation.RxScene
import com.nhaarman.acorn.presentation.SceneKey.Companion.defaultKey
import com.nhaarman.acorn.state.SceneState
import io.reactivex.rxkotlin.plusAssign

class ItemListScene(
    private val noteItemsRepository: NoteItemsRepository,
    private val listener: Events,
    savedState: SceneState?
) : RxScene<ItemListContainer>(savedState) {

    private val createClicks = view.whenAvailable { it.createClicks }
    private val itemClicks = view.whenAvailable { it.itemClicks }
    private val deleteClicks = view.whenAvailable { it.deleteClicks }

    private val items by lazy {
        noteItemsRepository.noteItems
            .observeOn(mainThread)
            .replay(1)
            .autoConnect(this)
    }

    override fun onStart() {
        super.onStart()

        disposables += items
            .combineWithLatestView()
            .subscribe { (items, view) ->
                view?.items = items
            }

        disposables += createClicks
            .subscribe { listener.createItemRequested() }

        disposables += itemClicks
            .subscribe { listener.showItemRequested(it) }

        disposables += deleteClicks
            .subscribe { noteItemsRepository.delete(it) }
    }

    override fun toString(): String {
        return "ItemListScene@${Integer.toHexString(hashCode())}"
    }

    interface Events {

        fun createItemRequested()
        fun showItemRequested(item: NoteItem)
    }

    companion object {

        val key = defaultKey<ItemListScene>()
    }
}