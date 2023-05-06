package com.nhaarman.acorn.notesapp.android

import com.nhaarman.acorn.Option
import com.nhaarman.acorn.notesapp.note.NoteItem
import com.nhaarman.acorn.notesapp.note.NoteItemsRepository
import com.nhaarman.acorn.toOption
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
                    is Event.DeleteId -> items.also { it.remove(event.id) }
                    is Event.Purge -> mutableMapOf()
                }
            }
            .map { it.values.toList() }
            .replay(1).refCount()
    }

    override fun create(text: String): Single<NoteItem> {
        return Single.fromCallable {
            NoteItem(availableId.incrementAndGet(), text)
                .also { eventSubject.onNext(Event.Create(it)) }
        }
    }

    override fun delete(itemId: Long) {
        eventSubject.onNext(Event.DeleteId(itemId))
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
        return Single.fromCallable {
            eventSubject.onNext(Event.Update(NoteItem(itemId, text)))
            true
        }
    }

    override fun purge() {
        eventSubject.onNext(Event.Purge)
    }

    private sealed class Event {
        class Create(val item: NoteItem) : Event()
        class Update(val item: NoteItem) : Event()
        class Delete(val item: NoteItem) : Event()
        class DeleteId(val id: Long) : Event()

        object Purge : Event()
    }
}
