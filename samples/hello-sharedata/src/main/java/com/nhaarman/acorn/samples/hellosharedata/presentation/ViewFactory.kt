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

import com.nhaarman.acorn.android.presentation.bindViews
import com.nhaarman.acorn.presentation.SceneKey
import com.nhaarman.acorn.samples.hellosharedata.R
import com.nhaarman.acorn.samples.hellosharedata.presentation.picturedetail.PictureDetailScene
import com.nhaarman.acorn.samples.hellosharedata.presentation.picturedetail.PictureDetailViewController
import com.nhaarman.acorn.samples.hellosharedata.presentation.picturegallery.PictureGalleryScene
import com.nhaarman.acorn.samples.hellosharedata.presentation.picturegallery.PictureGalleryViewController

val viewFactory = bindViews {

    bind(
        SceneKey.defaultKey<PictureGalleryScene>(),
        R.layout.picturegallery_scene,
        ::PictureGalleryViewController,
    )

    bind(
        SceneKey.defaultKey<PictureDetailScene>(),
        R.layout.picturedetail_scene,
        ::PictureDetailViewController,
    )
}
