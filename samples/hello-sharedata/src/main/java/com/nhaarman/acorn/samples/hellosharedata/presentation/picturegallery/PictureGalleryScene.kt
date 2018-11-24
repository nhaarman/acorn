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

package com.nhaarman.acorn.samples.hellosharedata.presentation.picturegallery

import com.nhaarman.acorn.presentation.RxScene
import com.nhaarman.acorn.samples.hellosharedata.pictures.Picture
import com.nhaarman.acorn.samples.hellosharedata.pictures.PicturesProvider
import com.nhaarman.acorn.state.SceneState
import io.reactivex.rxkotlin.plusAssign

class PictureGalleryScene(
    private val picturesProvider: PicturesProvider,
    private val listener: Events,
    savedState: SceneState? = null
) : RxScene<PictureGalleryContainer>(savedState) {

    override fun onStart() {
        super.onStart()

        disposables += picturesProvider.pictures
            .combineWithLatestView()
            .subscribe { (pictures, container) ->
                container?.pictures = pictures
            }
    }

    override fun attach(v: PictureGalleryContainer) {
        super.attach(v)

        v.addOnPictureSelectedListener { picture ->
            listener.galleryPictureClicked(picture)
        }
    }

    interface Events {

        fun galleryPictureClicked(picture: Picture)
    }
}
