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

package com.nhaarman.notesapp.mosby.note

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
}