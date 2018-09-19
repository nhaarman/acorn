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

import com.nhaarman.bravo.android.presentation.bindViews
import com.nhaarman.bravo.presentation.SceneKey
import com.nhaarman.bravo.samples.hellosharedata.R
import com.nhaarman.bravo.samples.hellosharedata.presentation.picturedetail.PictureDetailScene
import com.nhaarman.bravo.samples.hellosharedata.presentation.picturegallery.PictureGalleryScene

val viewFactory = bindViews {

    bind(SceneKey.defaultKey<PictureGalleryScene>(),
        R.layout.picturegallery_scene
    )
    bind(SceneKey.defaultKey<PictureDetailScene>(),
        R.layout.picturedetail_scene
    )
}

