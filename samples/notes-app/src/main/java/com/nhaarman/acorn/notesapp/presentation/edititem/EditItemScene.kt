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

package com.nhaarman.acorn.notesapp.presentation.edititem

import com.nhaarman.acorn.notesapp.mainThread
import com.nhaarman.acorn.notesapp.note.NoteItemsRepository
import com.nhaarman.acorn.presentation.RxScene
import com.nhaarman.acorn.presentation.SavableScene
import com.nhaarman.acorn.presentation.SceneKey.Companion.defaultKey
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.state.get
import io.reactivex.rxkotlin.plusAssign

class EditItemScene(
    private val itemId: Long,
    private val noteItemsRepository: NoteItemsRepository,
    private val listener: Events,
    savedState: SceneState? = null
) : RxScene<EditItemContainer>(savedState),
    SavableScene {

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
