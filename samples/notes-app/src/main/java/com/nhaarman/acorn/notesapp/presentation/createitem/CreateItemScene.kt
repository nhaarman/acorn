/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.notesapp.presentation.createitem

import com.nhaarman.bravo.notesapp.mainThread
import com.nhaarman.bravo.notesapp.note.NoteItem
import com.nhaarman.bravo.notesapp.note.NoteItemsRepository
import com.nhaarman.bravo.presentation.RxScene
import com.nhaarman.bravo.presentation.SceneKey.Companion.defaultKey
import com.nhaarman.bravo.state.SceneState
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom

class CreateItemScene(
    private val initialText: String?,
    private val noteItemsRepository: NoteItemsRepository,
    private val listener: Events,
    savedState: SceneState? = null
) : RxScene<CreateItemContainer>(savedState) {

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