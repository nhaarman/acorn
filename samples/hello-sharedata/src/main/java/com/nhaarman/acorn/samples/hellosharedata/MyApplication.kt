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

package com.nhaarman.acorn.samples.hellosharedata

import android.app.Application
import android.content.Context
import com.nhaarman.acorn.samples.hellosharedata.pictures.DevicePicturesProvider
import com.nhaarman.acorn.samples.hellosharedata.pictures.PicturesProvider
import com.nhaarman.acorn.samples.hellosharedata.presentation.MainNavigatorProvider
import com.nhaarman.acorn.samples.hellosharedata.presentation.PickPictureNavigatorProvider

val Context.mainNavigatorProvider get() = (applicationContext as MyApplication).mainNavigatorProvider
val Context.pickPictureNavigatorProvider get() = (applicationContext as MyApplication).pickPictureNavigatorProvider

val Context.picturesProvider get() = (applicationContext as MyApplication).picturesProvider

class MyApplication : Application() {

    val picturesProvider: PicturesProvider by lazy {
        DevicePicturesProvider(this)
    }

    val mainNavigatorProvider by lazy {
        MainNavigatorProvider(picturesProvider)
    }

    val pickPictureNavigatorProvider by lazy {
        PickPictureNavigatorProvider(picturesProvider)
    }
}