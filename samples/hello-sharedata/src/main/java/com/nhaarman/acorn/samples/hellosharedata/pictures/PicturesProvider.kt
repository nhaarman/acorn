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
