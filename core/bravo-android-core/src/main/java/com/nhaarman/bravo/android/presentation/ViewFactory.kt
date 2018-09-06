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

package com.nhaarman.bravo.android.presentation

import android.view.ViewGroup
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.presentation.SceneKey

/**
 * A factory interface that can create view instances for [Scene]s.
 */
interface ViewFactory {

    /**
     * Creates a [ViewResult] for given Scene key.
     *
     * @param sceneKey The key of the [Scene] instance for which the
     * corresponding view should be created.
     * @param parent If non-null, this is the parent view that the resulting
     * view should be attached to. The implementation must not add the view
     * to the parent itself, but this can be used to generate the LayoutParams
     * of the view.
     *
     * @return The resulting [ViewResult]. `null` if no result could be created
     * for given [sceneKey].
     */
    fun viewFor(sceneKey: SceneKey, parent: ViewGroup): ViewResult?
}