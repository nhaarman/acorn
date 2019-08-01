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

package com.nhaarman.acorn.notesapp.integration.presentation.itemlist

import com.nhaarman.acorn.notesapp.note.NoteItem
import com.nhaarman.acorn.notesapp.presentation.itemlist.ItemListContainer
import com.nhaarman.acorn.testing.RestorableTestContainer
import io.reactivex.subjects.PublishSubject

class TestItemListContainer : ItemListContainer, RestorableTestContainer {

    override var items: List<NoteItem> = emptyList()

    override val createClicks = PublishSubject.create<Unit>()

    override val itemClicks = PublishSubject.create<NoteItem>()

    override val deleteClicks = PublishSubject.create<NoteItem>()

    fun clickItem(position: Int) {
        itemClicks.onNext(items[position])
    }
}
