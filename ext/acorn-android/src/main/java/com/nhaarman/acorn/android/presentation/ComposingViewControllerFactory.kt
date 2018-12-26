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

import android.view.ViewGroup
import com.nhaarman.acorn.presentation.Scene

/**
 * A [ViewControllerFactory] implementation that can delegate to other implementations.
 */
class ComposingViewControllerFactory private constructor(
    private val sources: List<ViewControllerFactory>
) : ViewControllerFactory {

    override fun supports(scene: Scene<*>): Boolean {
        return sources.any { it.supports(scene) }
    }

    override fun viewControllerFor(scene: Scene<*>, parent: ViewGroup): ViewController {
        return sources
            .first { it.supports(scene) }
            .viewControllerFor(scene, parent)
    }

    companion object {

        fun from(sources: List<ViewControllerFactory>) = ComposingViewControllerFactory(sources)
        fun from(vararg sources: ViewControllerFactory) = ComposingViewControllerFactory(sources.asList())
    }
}