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

import com.nhaarman.acorn.presentation.BasicScene
import com.nhaarman.acorn.presentation.SavableScene
import com.nhaarman.acorn.samples.hellosharedata.pictures.Picture
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.state.get
import java.io.File

class PictureDetailScene private constructor(
    private val isPicking: Boolean,
    private val picture: Picture,
    private val listener: Events,
    savedState: SceneState? = null,
) : BasicScene<PictureDetailContainer>(savedState),
    SavableScene {

    override fun attach(v: PictureDetailContainer) {
        super.attach(v)

        v.picture = picture
        v.isPicking = isPicking

        v.setOnUpClickedListener { listener.upClicked() }
        v.setPicturePickedListener { listener.picturePicked(picture) }
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
