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

package com.nhaarman.acorn.samples.transitionanimation.image

import android.view.View
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.presentation.RestorableContainer
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_scene_contents.*

interface ImageContainer : RestorableContainer {

    var imageUrl: String?
}

class ImageViewController(override val view: View) : RestorableViewController, ImageContainer {

    override var imageUrl: String? = null
        set(value) {
//            view.imageView.setBackgroundColor(
//                Color.rgb(
//                    (Math.random() * 255).toInt(),
//                    (Math.random() * 255).toInt(),
//                    (Math.random() * 255).toInt()
//                )
//            )
//            return
            if (value == null) {
                imageView.setImageDrawable(null)
                return
            }

            Picasso.get()
                .load(value)
                .into(imageView)
        }
}