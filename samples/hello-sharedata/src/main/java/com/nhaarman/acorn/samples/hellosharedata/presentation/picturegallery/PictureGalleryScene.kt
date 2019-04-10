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

import com.nhaarman.acorn.presentation.RxScene
import com.nhaarman.acorn.presentation.SavableScene
import com.nhaarman.acorn.samples.hellosharedata.pictures.Picture
import com.nhaarman.acorn.samples.hellosharedata.pictures.PicturesProvider
import com.nhaarman.acorn.state.SceneState
import io.reactivex.rxkotlin.plusAssign

class PictureGalleryScene(
    private val picturesProvider: PicturesProvider,
    private val listener: Events,
    savedState: SceneState? = null
) : RxScene<PictureGalleryContainer>(savedState),
    SavableScene {

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
