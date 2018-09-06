/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.notesapp.conductor

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.db.SupportSQLiteOpenHelper
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.content.Context
import com.nhaarman.notesapp.conductor.note.NoteItemsRepository
import com.nhaarman.notesapp.conductor.note.SqlNoteItemsRepository
import com.squareup.sqlbrite3.SqlBrite
import io.reactivex.schedulers.Schedulers

class AndroidNoteAppComponent(
    private val context: Context
) {

    val noteItemsRepository: NoteItemsRepository by lazy {
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