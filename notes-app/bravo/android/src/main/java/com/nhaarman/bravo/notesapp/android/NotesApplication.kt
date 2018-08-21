package com.nhaarman.bravo.notesapp.android

import android.app.Application
import android.content.Context
import android.os.Looper
import com.nhaarman.bravo.android.TimberLogger
import com.nhaarman.bravo.notesapp.mainThread
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import timber.log.Timber.DebugTree

val Context.application get() = (applicationContext as NotesApplication)
val Context.notesApplication get() = application
val Context.noteAppComponent get() = (applicationContext as NotesApplication).noteAppComponent

class NotesApplication : Application() {

    init {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            AndroidSchedulers.from(Looper.getMainLooper(), true)
        }
        mainThread = AndroidSchedulers.mainThread()
        bravo.logger = TimberLogger()
        Timber.plant(DebugTree())
    }

    val noteAppComponent by lazy {
        AndroidNotesAppComponent(this)
    }

    val navigatorProvider by lazy {
        NotesAppNavigatorProvider(noteAppComponent)
    }
}