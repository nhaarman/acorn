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