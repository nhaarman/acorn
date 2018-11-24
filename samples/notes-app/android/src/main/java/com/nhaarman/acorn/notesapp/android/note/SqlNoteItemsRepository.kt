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

package com.nhaarman.bravo.notesapp.android.note

import androidx.sqlite.db.SupportSQLiteDatabase
import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import arrow.core.Option
import arrow.core.toOption
import com.nhaarman.bravo.notesapp.note.NoteItem
import com.nhaarman.bravo.notesapp.note.NoteItemsRepository
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
