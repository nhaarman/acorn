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

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import java.io.File

class PictureContentProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        error("Unsupported")
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        error("Unsupported")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        error("Unsupported")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        error("Unsupported")
    }

    override fun getType(uri: Uri): String? {
        error("Unsupported")
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        return ParcelFileDescriptor.open(
            File(uri.path),
            ParcelFileDescriptor.MODE_READ_ONLY
        )
    }

    companion object {

        private const val authority = "com.nhaarman.acorn.samples.hellosharedata"

        fun uriFor(file: File): Uri {
            return Uri.parse("content://${PictureContentProvider.authority}${file.absolutePath}")
        }
    }
}