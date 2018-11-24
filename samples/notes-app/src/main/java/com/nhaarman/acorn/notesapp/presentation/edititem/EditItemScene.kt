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

package com.nhaarman.acorn.notesapp.presentation.edititem

import com.nhaarman.acorn.notesapp.mainThread
import com.nhaarman.acorn.notesapp.note.NoteItemsRepository
import com.nhaarman.acorn.presentation.RxScene
import com.nhaarman.acorn.presentation.SceneKey.Companion.defaultKey
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.state.get
import io.reactivex.rxkotlin.plusAssign

class EditItemScene(
    private val itemId: Long,
    private val noteItemsRepository: NoteItemsRepository,
    private val listener: Events,
    savedState: SceneState? = null
) : RxScene<EditItemContainer>(savedState) {

    private val originalItem by lazy {
        noteItemsRepository.find(itemId)
            .observeOn(mainThread)
            .replay(1).autoConnect(this)
    }

    private val saveClicks = view.whenAvailable { it.saveClicks }
    private val deleteClicks = view.whenAvailable { it.deleteClicks }

    override fun onStart() {
        super.onStart()

        disposables += originalItem
            .combineWithLatestView()
            .firstElement()
            .subscribe { (item, view) ->
                view?.initialText = item.orNull()?.text
            }

        disposables += saveClicks
            .flatMapSingle { text -> noteItemsRepository.update(itemId, text) }
            .observeOn(mainThread)
            .subscribe { _ -> listener.saved() }

        disposables += deleteClicks
            .firstElement()
            .subscribe {
                noteItemsRepository.delete(itemId)
                listener.deleted()
            }
    }

    override fun saveInstanceState(): SceneState {
        return super.saveInstanceState()
            .also { it["item_id"] = itemId }
    }

    override fun toString(): String {
        return "EditItemScene(itemId=$itemId)@${Integer.toHexString(hashCode())}"
    }

    interface Events {

        fun saved()
        fun deleted()
    }

    companion object {

        val key = defaultKey<EditItemScene>()

        fun create(
            noteItemsRepository: NoteItemsRepository,
            listener: Events,
            state: SceneState
        ): EditItemScene {
            return EditItemScene(
                state["item_id"]!!,
                noteItemsRepository,
                listener,
                state
            )
        }
    }
}