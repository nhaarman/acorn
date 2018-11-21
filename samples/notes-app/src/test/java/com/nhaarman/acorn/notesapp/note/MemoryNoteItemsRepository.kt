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

package com.nhaarman.acorn.notesapp.note

import arrow.core.Option
import arrow.core.toOption
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.ReplaySubject
import java.util.concurrent.atomic.AtomicLong

class MemoryNoteItemsRepository : NoteItemsRepository {

    private val availableId = AtomicLong()

    private val eventSubject = ReplaySubject.create<Event>()

    override val noteItems: Observable<List<NoteItem>> by lazy {
        eventSubject
            .scan(mutableMapOf<Long, NoteItem>()) { items, event ->
                when (event) {
                    is Event.Create -> items.also { it[event.item.id] = event.item }
                    is Event.Update -> items.also { it[event.item.id] = event.item }
                    is Event.Delete -> items.also { it.remove(event.item.id) }
                }
            }
            .map { it.values.toList() }
            .replay(1).refCount()
    }

    override fun create(text: String): Single<NoteItem> {
        return Single
            .fromCallable {
                NoteItem(availableId.incrementAndGet(), text)
                    .also { eventSubject.onNext(Event.Create(it)) }
            }
    }

    override fun delete(itemId: Long) {
        error("Not used")
    }

    override fun delete(item: NoteItem) {
        eventSubject.onNext(Event.Delete(item))
    }

    override fun find(itemId: Long): Observable<Option<NoteItem>> {
        return noteItems
            .map {
                it
                    .find { item -> item.id == itemId }
                    .toOption()
            }
    }

    override fun update(itemId: Long, text: String): Single<Boolean> {
        return Single
            .fromCallable {
                eventSubject.onNext(Event.Update(NoteItem(itemId, text)))
                true
            }
    }

    override fun purge() {
        error("Not used")
    }

    private sealed class Event {
        class Create(val item: NoteItem) : Event()
        class Update(val item: NoteItem) : Event()
        class Delete(val item: NoteItem) : Event()
    }
}