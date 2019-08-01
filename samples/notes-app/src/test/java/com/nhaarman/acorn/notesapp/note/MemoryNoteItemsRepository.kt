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
