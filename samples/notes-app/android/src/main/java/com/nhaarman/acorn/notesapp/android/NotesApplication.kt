/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.acorn.notesapp.android

import android.app.Application
import android.content.Context
import android.os.Looper
import com.nhaarman.acorn.android.TimberLogger
import com.nhaarman.acorn.notesapp.mainThread
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
        acorn.logger = TimberLogger()
        Timber.plant(DebugTree())
    }

    val noteAppComponent by lazy {
        AndroidNotesAppComponent(this)
    }

    val navigatorProvider by lazy {
        NotesAppNavigatorProvider(noteAppComponent)
    }
}