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
