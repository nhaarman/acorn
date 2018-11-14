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