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

package com.nhaarman.acorn.samples.hellosharedata.presentation.picturegallery

import android.view.View
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.presentation.RestorableContainer
import com.nhaarman.acorn.samples.hellosharedata.pictures.Picture
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.picturegallery_scene.*

interface PictureGalleryContainer : RestorableContainer {

    var pictures: List<Picture>

    fun addOnPictureSelectedListener(f: (Picture) -> Unit)
}

class PictureGalleryViewController(
    override val view: View
) : RestorableViewController, PictureGalleryContainer, LayoutContainer {

    override var pictures: List<Picture> = emptyList()
        set(value) {
            picturesRecyclerView.pictures = value
        }

    override fun addOnPictureSelectedListener(f: (Picture) -> Unit) {
        picturesRecyclerView.addOnPictureSelectedListener(f)
    }

    override val containerView = view
}
