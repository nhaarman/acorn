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
