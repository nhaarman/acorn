package com.nhaarman.notesapp.mosby.presentation

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.nhaarman.notesapp.mosby.note.NoteItem

interface ItemListView : MvpView {

    var items: List<NoteItem>
}
