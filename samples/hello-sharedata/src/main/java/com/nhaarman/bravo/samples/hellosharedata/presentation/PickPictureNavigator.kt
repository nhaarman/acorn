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

package com.nhaarman.bravo.samples.hellosharedata.presentation

import com.nhaarman.bravo.navigation.StackNavigator
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.samples.hellosharedata.pictures.Picture
import com.nhaarman.bravo.samples.hellosharedata.pictures.PicturesProvider
import com.nhaarman.bravo.samples.hellosharedata.presentation.picturedetail.PictureDetailScene
import com.nhaarman.bravo.samples.hellosharedata.presentation.picturegallery.PictureGalleryScene
import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.state.SceneState

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

    override fun instantiateScene(sceneClass: Class<Scene<*>>, state: SceneState?): Scene<out Container> {
        return when (sceneClass) {
            PictureGalleryScene::class.java -> PictureGalleryScene(picturesProvider, this, state)
            PictureDetailScene::class.java -> PictureDetailScene.pick(state!!, this)
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