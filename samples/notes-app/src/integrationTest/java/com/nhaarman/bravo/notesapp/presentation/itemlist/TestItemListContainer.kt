package com.nhaarman.bravo.notesapp.presentation.itemlist

import com.nhaarman.bravo.notesapp.note.NoteItem
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