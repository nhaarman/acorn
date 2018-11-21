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

package com.nhaarman.acorn.samples.hellosharedata.presentation.picturedetail

import android.view.View
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.presentation.RestorableContainer
import com.nhaarman.acorn.samples.hellosharedata.R
import com.nhaarman.acorn.samples.hellosharedata.pictures.Picture
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.picturedetail_scene.*

interface PictureDetailContainer : RestorableContainer {

    var picture: Picture?
    var isPicking: Boolean

    fun setOnUpClickedListener(function: () -> Unit)
    fun setPicturePickedListener(function: () -> Unit)
}

class PictureDetailViewController(
    override val view: View
) : RestorableViewController, PictureDetailContainer {

    override var picture: Picture? = null
        set(value) {
            if (value == null) {
                pictureDetailIV.setImageDrawable(null)
                return
            }

            toolbar.title = value.file.name

            Picasso.get()
                .load(value.file)
                .into(pictureDetailIV)
        }

    override var isPicking: Boolean = false
        set(value) {
            if (field == value) return
            field = value

            if (value) {
                toolbar.inflateMenu(R.menu.pickpicture)
            }
        }

    override fun setOnUpClickedListener(function: () -> Unit) {
        toolbar.setNavigationOnClickListener { function.invoke() }
    }

    override fun setPicturePickedListener(function: () -> Unit) {
        toolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.pick) {
                function.invoke()
                true
            } else {
                false
            }
        }
    }
}