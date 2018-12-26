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

package com.nhaarman.acorn.samples.hellosharedata.presentation

import com.nhaarman.acorn.navigation.StackNavigator
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.samples.hellosharedata.pictures.Picture
import com.nhaarman.acorn.samples.hellosharedata.pictures.PicturesProvider
import com.nhaarman.acorn.samples.hellosharedata.presentation.picturedetail.PictureDetailScene
import com.nhaarman.acorn.samples.hellosharedata.presentation.picturegallery.PictureGalleryScene
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.SceneState
import kotlin.reflect.KClass

class MainNavigator(
    private val picturesProvider: PicturesProvider,
    savedState: NavigatorState? = null
) : StackNavigator(savedState),
    PictureGalleryScene.Events,
    PictureDetailScene.Events {

    override fun initialStack(): List<Scene<out Container>> {
        return listOf(PictureGalleryScene(picturesProvider, this))
    }

    override fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<out Container> {
        return when (sceneClass) {
            PictureGalleryScene::class -> PictureGalleryScene(picturesProvider, this, state)
            PictureDetailScene::class -> PictureDetailScene.view(state!!, this)
            else -> error("Unknown Scene class: $sceneClass")
        }
    }

    override fun galleryPictureClicked(picture: Picture) {
        push(PictureDetailScene.view(picture, this))
    }

    override fun upClicked() {
        pop()
    }

    override fun picturePicked(picture: Picture) {
        // No-op
    }
}