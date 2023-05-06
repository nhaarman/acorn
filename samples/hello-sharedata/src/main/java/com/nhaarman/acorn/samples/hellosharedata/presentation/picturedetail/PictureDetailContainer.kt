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

package com.nhaarman.acorn.samples.hellosharedata.presentation.picturedetail

import android.view.View
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.presentation.RestorableContainer
import com.nhaarman.acorn.samples.hellosharedata.R
import com.nhaarman.acorn.samples.hellosharedata.databinding.PicturedetailSceneBinding
import com.nhaarman.acorn.samples.hellosharedata.pictures.Picture
import com.squareup.picasso.Picasso

interface PictureDetailContainer : RestorableContainer {

    var picture: Picture?
    var isPicking: Boolean

    fun setOnUpClickedListener(function: () -> Unit)
    fun setPicturePickedListener(function: () -> Unit)
}

class PictureDetailViewController(
    override val view: View,
) : RestorableViewController, PictureDetailContainer {

    private val binding = PicturedetailSceneBinding.bind(view)

    override var picture: Picture? = null
        set(value) {
            if (value == null) {
                binding.pictureDetailIV.setImageDrawable(null)
                return
            }

            binding.toolbar.title = value.file.name

            Picasso.get()
                .load(value.file)
                .into(binding.pictureDetailIV)
        }

    override var isPicking: Boolean = false
        set(value) {
            if (field == value) return
            field = value

            if (value) {
                binding.toolbar.inflateMenu(R.menu.pickpicture)
            }
        }

    override fun setOnUpClickedListener(function: () -> Unit) {
        binding.toolbar.setNavigationOnClickListener { function.invoke() }
    }

    override fun setPicturePickedListener(function: () -> Unit) {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.pick) {
                function.invoke()
                true
            } else {
                false
            }
        }
    }
}
