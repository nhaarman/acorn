package com.nhaarman.bravo.notesapp.presentation.itemlist

import com.nhaarman.bravo.notesapp.note.NoteItem
import com.nhaarman.bravo.state.ContainerState
import io.reactivex.subjects.PublishSubject

class TestItemListContainer : ItemListContainer {

    override var items: List<NoteItem> = emptyList()

    override val createClicks = PublishSubject.create<Unit>()

    override val itemClicks = PublishSubject.create<NoteItem>()

    override val deleteClicks = PublishSubject.create<NoteItem>()

    override fun saveInstanceState(): ContainerState {
        return ContainerState()
    }

    override fun restoreInstanceState(bundle: ContainerState) {
    }

    fun clickItem(position: Int) {
        itemClicks.onNext(items[position])
    }
}