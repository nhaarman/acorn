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

class PickPictureNavigator(
    private val picturesProvider: PicturesProvider,
    savedState: NavigatorState? = null
) : StackNavigator(savedState),
    PictureGalleryScene.Events,
    PictureDetailScene.Events {

    private var listeners = listOf<Events>()

    fun addListener(listener: Events) {
        listeners += listener
    }

    fun removeListener(listener: Events) {
        listeners -= listener
    }

    override fun initialStack(): List<Scene<out Container>> {
        return listOf(PictureGalleryScene(picturesProvider, this))
    }

    override fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<out Container> {
        return when (sceneClass) {
            PictureGalleryScene::class -> PictureGalleryScene(picturesProvider, this, state)
            PictureDetailScene::class -> PictureDetailScene.pick(state!!, this)
            else -> error("Unknown Scene class: $sceneClass")
        }
    }

    override fun galleryPictureClicked(picture: Picture) {
        push(PictureDetailScene.pick(picture, this))
    }

    override fun upClicked() {
        pop()
    }

    override fun picturePicked(picture: Picture) {
        listeners.forEach { it.picturePicked(picture) }
        finish()
    }

    interface Events {

        fun picturePicked(picture: Picture)
    }
}