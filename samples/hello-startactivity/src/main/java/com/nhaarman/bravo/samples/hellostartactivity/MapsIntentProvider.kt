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
import com.nhaarman.bravo.android.presentation.IntentProvider
import com.nhaarman.bravo.navigation.TransitionData
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

object MapsIntentProvider : IntentProvider {

    override fun intentFor(scene: Scene<out Container>, data: TransitionData?): Intent? {
        if (scene !is MapsScene) return null

        return Intent(Intent.ACTION_VIEW).apply {
            setData(Uri.parse("https://www.google.com/maps/search/?api=1&query=taj+mahal+agra"))
        }
    }

    override fun onActivityResult(scene: Scene<out Container>, resultCode: Int, data: Intent?): Boolean {
        if (scene !is MapsScene) return false

        scene.finished()
        return true
    }
}