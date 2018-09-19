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

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nhaarman.bravo.android.BravoActivityDelegate
import com.nhaarman.bravo.samples.hellosharedata.pictures.Picture
import com.nhaarman.bravo.samples.hellosharedata.pictures.PictureContentProvider
import com.nhaarman.bravo.samples.hellosharedata.presentation.PickPictureNavigator
import com.nhaarman.bravo.samples.hellosharedata.presentation.viewFactory

/**
 * The Activity that is started when this application is started to pick a
 * picture.
 */
class PickPictureActivity : AppCompatActivity(), PickPictureNavigator.Events {

    private val delegate by lazy {
        BravoActivityDelegate.from(
            activity = this,
            navigatorProvider = pickPictureNavigatorProvider,
            viewFactory = viewFactory
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.onCreate(savedInstanceState)

        (delegate.navigator as PickPictureNavigator).addListener(this)
    }

    override fun onStart() {
        super.onStart()
        delegate.onStart()
    }

    override fun picturePicked(picture: Picture) {
        setResult(Activity.RESULT_OK, Intent().apply {
            data = PictureContentProvider.uriFor(picture.file)
        })
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        delegate.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        picturesProvider.onPermissionChanged()
    }

    override fun onStop() {
        super.onStop()
        delegate.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        (delegate.navigator as PickPictureNavigator).removeListener(this)
        delegate.onDestroy()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        delegate.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (!delegate.onBackPressed()) {
            super.onBackPressed()
        }
    }
}