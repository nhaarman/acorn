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

package com.nhaarman.bravo.samples.hellosharedata.presentation.picturedetail

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import com.nhaarman.bravo.android.presentation.RestorableView
import com.nhaarman.bravo.presentation.RestorableContainer
import com.nhaarman.bravo.samples.hellosharedata.R
import com.nhaarman.bravo.samples.hellosharedata.pictures.Picture
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.picturedetail_scene.view.*

interface PictureDetailContainer : RestorableContainer {

    var picture: Picture?
    var isPicking: Boolean

    fun setOnUpClickedListener(function: () -> Unit)
    fun setPicturePickedListener(function: () -> Unit)
}

class PictureDetailView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), PictureDetailContainer, RestorableView {

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