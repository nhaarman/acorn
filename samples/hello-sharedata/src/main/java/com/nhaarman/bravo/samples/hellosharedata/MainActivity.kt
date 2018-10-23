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

package com.nhaarman.bravo.samples.hellosharedata

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.nhaarman.bravo.android.BravoAppCompatActivity
import com.nhaarman.bravo.android.navigation.NavigatorProvider
import com.nhaarman.bravo.android.presentation.ViewFactory
import com.nhaarman.bravo.samples.hellosharedata.presentation.viewFactory

/**
 * The Activity that is started when the application is launched from the
 * launcher.
 */
class MainActivity : BravoAppCompatActivity() {

    override fun provideNavigatorProvider(): NavigatorProvider {
        return mainNavigatorProvider
    }

    override fun provideViewFactory(): ViewFactory {
        return viewFactory
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        picturesProvider.onPermissionChanged()
    }

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), 42)
        }
    }
}