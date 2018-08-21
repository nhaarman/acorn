package com.nhaarman.notesapp.cicerone

import android.app.Application
import android.content.Context
import ru.terrakok.cicerone.Cicerone

val Context.application get() = (applicationContext as NoteApplication)
val Context.noteAppComponent get() = application.noteAppComponent
val Context.cicerone get() = application.cicerone

class NoteApplication : Application() {

    val noteAppComponent by lazy { AndroidNoteAppComponent(this) }

    val cicerone by lazy {
        Cicerone.create()
    }
}