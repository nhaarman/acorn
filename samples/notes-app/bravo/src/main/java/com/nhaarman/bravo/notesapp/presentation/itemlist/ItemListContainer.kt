package com.nhaarman.bravo.notesapp.presentation.itemlist

import com.nhaarman.bravo.presentation.RestorableContainer
import com.nhaarman.bravo.notesapp.note.NoteItem
import com.nhaarman.bravo.presentation.Container
import io.reactivex.Observable

interface ItemListContainer : Container, RestorableContainer {

    var items: List<NoteItem>

    val createClicks: Observable<Unit>
    val itemClicks: Observable<NoteItem>
    val deleteClicks: Observable<NoteItem>
}