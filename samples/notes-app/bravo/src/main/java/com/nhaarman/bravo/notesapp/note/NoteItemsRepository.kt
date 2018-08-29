package com.nhaarman.bravo.notesapp.note

import arrow.core.Option
import io.reactivex.Observable
import io.reactivex.Single

interface NoteItemsRepository {

    val noteItems: Observable<List<NoteItem>>

    fun create(text: String): Single<NoteItem>

    fun delete(itemId: Long)
    fun delete(item: NoteItem)

    fun find(itemId: Long): Observable<Option<NoteItem>>

    fun update(itemId: Long, text: String): Single<Boolean>

    fun purge()
}