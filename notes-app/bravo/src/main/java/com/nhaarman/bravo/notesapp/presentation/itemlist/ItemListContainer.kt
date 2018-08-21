package com.nhaarman.bravo.notesapp.presentation.itemlist

import com.nhaarman.bravo.notesapp.note.NoteItem
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.StateRestorable
import io.reactivex.Observable

interface ItemListContainer : Container, StateRestorable {

    var items: List<NoteItem>

    val createClicks: Observable<Unit>
    val itemClicks: Observable<NoteItem>
    val deleteClicks: Observable<NoteItem>
}