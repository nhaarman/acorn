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

package com.nhaarman.acorn.android.presentation

import android.view.View
import android.view.ViewGroup
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene

/**
 * A [Scene] that implements [ProvidesView].
 */
interface ViewProvidingScene<V : Container> : Scene<V>, ProvidesView

/**
 * A convenience interface that can be used to make [Scene] instances provide
 * their own [ViewController]s. It implements [ViewControllerFactory] and takes
 * away some of the boilerplate code when working with Scenes.
 *
 * This interface must only be used in conjunction with Scenes.
 */
interface ProvidesView : ViewControllerFactory {

    override fun supports(scene: Scene<*>): Boolean {
        return scene == this
    }

    override fun viewControllerFor(scene: Scene<*>, parent: ViewGroup): ViewController {
        return createViewController(parent)
    }

    /**
     * Creates the [ViewController].
     *
     * This method can be called zero or multiple times in a Scene's lifetime,
     * for example when the device is rotated. Do not keep a reference to the
     * result yourself, use [Scene.attach] and [Scene.detach] to obtain references.
     *
     * @param parent The parent [ViewGroup] that the resulting [View] should be
     * attached to. The implementation should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     */
    fun createViewController(parent: ViewGroup): ViewController
}