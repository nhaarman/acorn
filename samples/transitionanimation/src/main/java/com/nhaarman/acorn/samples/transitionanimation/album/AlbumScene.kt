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

package com.nhaarman.acorn.samples.transitionanimation.album

import android.view.ViewGroup
import com.nhaarman.acorn.android.presentation.ProvidesView
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.util.inflate
import com.nhaarman.acorn.presentation.BaseSavableScene
import com.nhaarman.acorn.samples.transitionanimation.R
import com.nhaarman.acorn.state.SceneState

class AlbumScene(
    private val events: Events,
    savedState: SceneState?
) : BaseSavableScene<AlbumContainer>(savedState), ProvidesView {

    override fun createViewController(parent: ViewGroup): ViewController {
        return AlbumViewController(parent.inflate(R.layout.album_scene))
    }

    override fun attach(v: AlbumContainer) {
        super.attach(v)

        v.imageUrls = (0..100).map { "https://picsum.photos/600/400/?$it" }

        v.setOnImageClickListener { url ->
            events.onImageClicked(url)
        }
    }

    interface Events {

        fun onImageClicked(imageUrl: String)
    }
}