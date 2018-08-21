package com.nhaarman.bravo.notesapp

import com.nhaarman.bravo.notesapp.note.NoteItemsRepository

interface NotesAppComponent {

    val noteItemsRepository: NoteItemsRepository
}