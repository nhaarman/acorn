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

package com.nhaarman.bravo.samples.hellostartactivity

import android.content.Intent
import android.net.Uri
import com.nhaarman.bravo.android.presentation.ExternalSceneIntentProvider
import com.nhaarman.bravo.navigation.TransitionData

object MapsIntentProvider : ExternalSceneIntentProvider<MapsScene>(MapsScene::class) {

    override fun intentFor(scene: MapsScene, data: TransitionData?): Intent? {
        return Intent(Intent.ACTION_VIEW).apply {
            setData(Uri.parse("https://www.google.com/maps/search/?api=1&query=taj+mahal+agra"))
        }
    }
}