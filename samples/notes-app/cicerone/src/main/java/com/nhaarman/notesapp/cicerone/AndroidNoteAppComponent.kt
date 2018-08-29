package com.nhaarman.notesapp.cicerone

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.db.SupportSQLiteOpenHelper
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.content.Context
import com.nhaarman.notesapp.cicerone.note.NoteItemsRepository
import com.nhaarman.notesapp.cicerone.note.SqlNoteItemsRepository
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