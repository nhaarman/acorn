/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
