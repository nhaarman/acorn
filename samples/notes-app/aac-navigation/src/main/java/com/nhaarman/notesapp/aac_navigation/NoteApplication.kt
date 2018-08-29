package com.nhaarman.notesapp.aac_navigation

import android.app.Application
import android.content.Context

val Context.application get() = (applicationContext as NoteApplication)
val Context.noteAppComponent get() = (applicationContext as NoteApplication).noteAppComponent

class NoteApplication : Application() {

    val noteAppComponent by lazy { AndroidNoteAppComponent(this) }
}