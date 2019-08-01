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

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import com.nhaarman.acorn.notesapp.NotesAppComponent
import com.nhaarman.acorn.notesapp.android.note.SqlNoteItemsRepository
import com.nhaarman.acorn.notesapp.note.NoteItemsRepository
import com.squareup.sqlbrite3.SqlBrite
import io.reactivex.schedulers.Schedulers

class AndroidNotesAppComponent(
    private val context: Context
) : NotesAppComponent {

    override val noteItemsRepository: NoteItemsRepository by lazy {
        SqlBrite.Builder().build()
            .wrapDatabaseHelper(openHelper("note-app"), Schedulers.io())
            .let(::SqlNoteItemsRepository)
    }

    private fun openHelper(name: String): SupportSQLiteOpenHelper {
        return FrameworkSQLiteOpenHelperFactory()
            .create(
                SupportSQLiteOpenHelper.Configuration.builder(context)
                    .name(name)
                    .callback(object : SupportSQLiteOpenHelper.Callback(1) {

                        override fun onCreate(db: SupportSQLiteDatabase) {
                            (1..version)
                                .forEach {
                                    onUpgrade(db, it - 1, it)
                                }
                        }

                        override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                            SqlNoteItemsRepository.onUpgrade(db, newVersion)
                        }
                    })
                    .build()
            )
    }
}
