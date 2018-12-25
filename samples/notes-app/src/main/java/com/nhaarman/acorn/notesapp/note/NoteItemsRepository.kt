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