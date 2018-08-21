package com.nhaarman.notesapp.mosby.presentation

import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState
import com.nhaarman.notesapp.mosby.note.NoteItem

class ItemListViewState : ViewState<ItemListView> {

    var items: List<NoteItem> = emptyList()

    override fun apply(view: ItemListView, retained: Boolean) {
        view.items = items
    }
}