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

package com.nhaarman.acorn.notesapp.android.note

import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import androidx.sqlite.db.SupportSQLiteDatabase
import arrow.core.Option
import arrow.core.toOption
import com.nhaarman.acorn.notesapp.note.NoteItem
import com.nhaarman.acorn.notesapp.note.NoteItemsRepository
import com.squareup.sqlbrite3.BriteDatabase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

internal class SqlNoteItemsRepository(
    private val database: BriteDatabase
) : NoteItemsRepository {

    override val noteItems: Observable<List<NoteItem>> by lazy {
        database
            .createQuery("note_items", "SELECT * FROM note_items")
            .mapToList { cursor ->
                NoteItem(
                    id = cursor.getLong(cursor.getColumnIndex("id")),
                    text = cursor.getString(cursor.getColumnIndex("text"))
                )
            }
            .replay(1).refCount()
    }

    override fun create(text: String): Single<NoteItem> {
        return Single
            .fromCallable {
                val id = database
                    .insert(
                        "note_items",
                        SQLiteDatabase.CONFLICT_NONE,
                        contentValuesOf("text" to text)
                    )
                NoteItem(id = id, text = text)
            }
            .subscribeOn(Schedulers.io())
    }

    override fun delete(itemId: Long) {
        Completable
            .fromAction {
                database.delete("note_items", "id=?", "$itemId")
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    override fun delete(item: NoteItem) {
        delete(item.id)
    }

    override fun find(itemId: Long): Observable<Option<NoteItem>> {
        return database
            .createQuery("note_items", "SELECT * FROM note_items WHERE id=? LIMIT 1", itemId)
            .mapToList { cursor ->
                NoteItem(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    text = cursor.getString(cursor.getColumnIndexOrThrow("text"))
                )
            }
            .map { it.firstOrNull().toOption() }
    }

    override fun update(itemId: Long, text: String): Single<Boolean> {
        return Single
            .fromCallable {
                val affected = database
                    .update(
                        "note_items",
                        SQLiteDatabase.CONFLICT_NONE,
                        contentValuesOf("text" to text),
                        "id=?",
                        "$itemId"
                    )

                affected != 0
            }
            .subscribeOn(Schedulers.io())
    }

    override fun purge() {
        database.delete("note_items", null)
    }

    companion object {

        fun onUpgrade(db: SupportSQLiteDatabase, newVersion: Int) {
            when (newVersion) {
                1 -> createItemsTable(db)
            }
        }

        private fun createItemsTable(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE note_items(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    text TEXT
                )
            """
            )

            (1..100)
                .forEach {
                    db
                        .insert(
                            "note_items",
                            SQLiteDatabase.CONFLICT_NONE,
                            contentValuesOf("text" to "$it")
                        )
                }
        }
    }
}
