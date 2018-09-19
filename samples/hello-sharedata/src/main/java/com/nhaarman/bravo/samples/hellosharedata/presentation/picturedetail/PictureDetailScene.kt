/*
 * Bravo - Decoupling navigation view Android
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

import com.nhaarman.bravo.presentation.BaseSaveableScene
import com.nhaarman.bravo.samples.hellosharedata.pictures.Picture
import com.nhaarman.bravo.state.SceneState
import com.nhaarman.bravo.state.get
import java.io.File

class PictureDetailScene private constructor(
    private val isPicking: Boolean,
    private val picture: Picture,
    private val listener: Events,
    savedState: SceneState? = null
) : BaseSaveableScene<PictureDetailContainer>(savedState) {

    override fun attach(v: PictureDetailContainer) {
        super.attach(v)

        v.picture = picture
        v.isPicking = isPicking

        v.setOnUpClickedListener { listener.upClicked() }
        v.setPicturePickedListener { listener.picturePicked(picture)}
    }

    override fun saveInstanceState(): SceneState {
        return super.saveInstanceState()
            .also { it.picture = picture }
    }

    interface Events {

        fun upClicked()
        fun picturePicked(picture: Picture)
    }

    companion object {

        fun view(picture: Picture, listener: Events): PictureDetailScene {
            return PictureDetailScene(false, picture, listener)
        }

        fun view(savedState: SceneState, listener: Events): PictureDetailScene {
            return PictureDetailScene(false, savedState.picture!!, listener, savedState)
        }

        fun pick(picture: Picture, listener: Events): PictureDetailScene {
            return PictureDetailScene(true, picture, listener)
        }

        fun pick(savedState: SceneState, listener: Events): PictureDetailScene {
            return PictureDetailScene(true, savedState.picture!!, listener, savedState)
        }

        private var SceneState.picture: Picture?
            get() = get<String>("picture_path")?.let { Picture(File(it)) }
            set(picture) {
                this["picture_path"] = picture?.file?.absolutePath
            }
    }
}
