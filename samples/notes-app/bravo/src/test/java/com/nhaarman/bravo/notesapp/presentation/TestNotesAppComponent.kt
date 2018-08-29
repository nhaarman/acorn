package com.nhaarman.bravo.notesapp.presentation

import com.nhaarman.bravo.notesapp.NotesAppComponent
import com.nhaarman.bravo.notesapp.note.MemoryNoteItemsRepository
import com.nhaarman.bravo.notesapp.note.NoteItemsRepository

class TestNotesAppComponent : NotesAppComponent {

    override val noteItemsRepository: NoteItemsRepository by lazy {
        MemoryNoteItemsRepository()
    }
}