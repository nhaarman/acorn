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

package com.nhaarman.acorn.notesapp.presentation.createitem

import com.nhaarman.acorn.notesapp.mainThread
import com.nhaarman.acorn.notesapp.note.NoteItem
import com.nhaarman.acorn.notesapp.note.NoteItemsRepository
import com.nhaarman.acorn.presentation.RxScene
import com.nhaarman.acorn.presentation.SavableScene
import com.nhaarman.acorn.presentation.SceneKey.Companion.defaultKey
import com.nhaarman.acorn.state.SceneState
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom

class CreateItemScene(
    private val initialText: String?,
    private val noteItemsRepository: NoteItemsRepository,
    private val listener: Events,
    savedState: SceneState? = null,
) : RxScene<CreateItemContainer>(savedState),
    SavableScene {

    private val textObservable by lazy {
        Observable.just(initialText ?: "")
            .concatWith(view.whenAvailable { it.textChanges })
            .replay(1).autoConnect(this)
    }

    private val createClicks = view.whenAvailable { it.createClicks }

    override fun onStart() {
        super.onStart()

        disposables += createClicks
            .withLatestFrom(textObservable) { _, text -> text }
            .firstElement()
            .flatMapSingle { text -> noteItemsRepository.create(text) }
            .observeOn(mainThread)
            .subscribe { noteItem -> listener.created(noteItem) }
    }

    override fun attach(v: CreateItemContainer) {
        super.attach(v)
        v.setInitialText(initialText)
    }

    override fun toString(): String {
        return "CreateItemScene@${Integer.toHexString(hashCode())}"
    }

    interface Events {

        fun created(noteItem: NoteItem)
    }

    companion object {

        val key = defaultKey<CreateItemScene>()
    }
}
