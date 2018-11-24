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

package com.nhaarman.bravo.notesapp.integration.presentation.itemlist

import com.nhaarman.bravo.notesapp.note.NoteItem
import com.nhaarman.bravo.notesapp.presentation.itemlist.ItemListContainer
import com.nhaarman.bravo.testing.RestorableTestContainer
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