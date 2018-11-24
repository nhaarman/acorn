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

package com.nhaarman.acorn.samples.hellosharedata.pictures

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.Cursor
import android.provider.MediaStore.Images.Media.BUCKET_DISPLAY_NAME
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.MediaColumns
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.database.getStringOrNull
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.File

interface PicturesProvider {

    val pictures: Observable<List<Picture>>

    fun onPermissionChanged()
}

class DevicePicturesProvider(
    private val context: Context
) : PicturesProvider {

    private val refreshSubject = PublishSubject.create<Unit>()

    override val pictures: Observable<List<Picture>> by lazy {
        refreshSubject
            .startWith(Unit)
            .map { getPictures() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .replay(1).refCount()
    }

    override fun onPermissionChanged() {
        refreshSubject.onNext(Unit)
    }

    @Suppress("NAME_SHADOWING")
    private fun getPictures(): List<Picture> {
        if (checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            return emptyList()
        }

        val projection = arrayOf(MediaColumns.DATA, BUCKET_DISPLAY_NAME)

        val cursor: Cursor? = context.contentResolver.query(EXTERNAL_CONTENT_URI, projection, null, null, null)
        if (cursor == null) return emptyList()

        cursor.use { cursor ->
            var result = listOf<Picture>()
            val index = cursor.getColumnIndexOrThrow(MediaColumns.DATA)
            while (cursor.moveToNext() && result.size < 10) {
                cursor.getStringOrNull(index)
                    ?.let { result += Picture(File(it)) }
            }
            return result
        }
    }
}
