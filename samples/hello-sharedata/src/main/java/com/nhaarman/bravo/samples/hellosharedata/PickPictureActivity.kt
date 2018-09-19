/*
 * Bravo - Decoupling navigation view Android
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

package com.nhaarman.bravo.samples.hellosharedata

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.nhaarman.bravo.android.BravoAppCompatActivity
import com.nhaarman.bravo.android.navigation.NavigatorProvider
import com.nhaarman.bravo.android.presentation.ViewFactory
import com.nhaarman.bravo.samples.hellosharedata.pictures.Picture
import com.nhaarman.bravo.samples.hellosharedata.pictures.PictureContentProvider
import com.nhaarman.bravo.samples.hellosharedata.presentation.PickPictureNavigator
import com.nhaarman.bravo.samples.hellosharedata.presentation.viewFactory

/**
 * The Activity that is started when this application is started to pick a
 * picture.
 */
class PickPictureActivity : BravoAppCompatActivity(), PickPictureNavigator.Events {

    override fun provideNavigatorProvider(): NavigatorProvider {
        return pickPictureNavigatorProvider
    }

    override fun provideViewFactory(): ViewFactory {
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