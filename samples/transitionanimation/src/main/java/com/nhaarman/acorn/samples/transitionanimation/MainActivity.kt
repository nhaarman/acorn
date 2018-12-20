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

package com.nhaarman.acorn.samples.transitionanimation

import com.nhaarman.acorn.android.AcornAppCompatActivity
import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.transition.TransitionFactory
import com.nhaarman.acorn.android.transition.transitionFactory
import com.nhaarman.acorn.samples.transitionanimation.album.AlbumScene
import com.nhaarman.acorn.samples.transitionanimation.image.ImageScene

class MainActivity : AcornAppCompatActivity() {

    override fun provideNavigatorProvider(): NavigatorProvider {
        return TransitionAnimationNavigatorProvider
    }

    override fun provideTransitionFactory(viewControllerFactory: ViewControllerFactory): TransitionFactory {
        return transitionFactory(viewControllerFactory) {
            (AlbumScene::class to ImageScene::class) use AlbumImageTransition
            (ImageScene::class to AlbumScene::class) use ImageAlbumTransition
        }
    }
}