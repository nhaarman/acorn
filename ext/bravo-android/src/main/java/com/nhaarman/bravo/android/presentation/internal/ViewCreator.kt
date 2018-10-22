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

package com.nhaarman.bravo.android.presentation.internal

import android.support.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup
import com.nhaarman.bravo.android.presentation.ViewController
import com.nhaarman.bravo.android.util.inflate
import com.nhaarman.bravo.presentation.Container

/**
 * Designates a class that can create a [View] and [Container] instance when
 * needed.
 */
internal interface ViewCreator {

    /**
     * Creates the [View] and [Container] instances.
     *
     * @param parent The parent [ViewGroup] the result will be added to.
     * Implementers must not add the result to the parent manually.
     */
    fun create(parent: ViewGroup): ViewController
}

internal class ViewControllerViewCreator<V : View>(
    @LayoutRes private val layoutResId: Int,
    private val wrapper: (V) -> ViewController
) : ViewCreator {

    override fun create(parent: ViewGroup): ViewController {
        return parent
            .inflate<V>(layoutResId)
            .let { view -> wrapper.invoke(view) }
    }
}