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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.nhaarman.acorn.android.AcornAppCompatActivity
import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.samples.hellosharedata.pictures.Picture
import com.nhaarman.acorn.samples.hellosharedata.pictures.PictureContentProvider
import com.nhaarman.acorn.samples.hellosharedata.presentation.PickPictureNavigator
import com.nhaarman.acorn.samples.hellosharedata.presentation.viewFactory

/**
 * The Activity that is started when this application is started to pick a
 * picture.
 */
class PickPictureActivity : AcornAppCompatActivity(), PickPictureNavigator.Events {

    override fun provideNavigatorProvider(): NavigatorProvider {
        return pickPictureNavigatorProvider
    }

    override fun provideViewControllerFactory(): ViewControllerFactory {
        return viewFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (navigator() as PickPictureNavigator).addListener(this)
    }

    override fun picturePicked(picture: Picture) {
        setResult(Activity.RESULT_OK, Intent().apply {
            data = PictureContentProvider.uriFor(picture.file)
        })
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        picturesProvider.onPermissionChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        (navigator() as PickPictureNavigator).removeListener(this)
    }
}
