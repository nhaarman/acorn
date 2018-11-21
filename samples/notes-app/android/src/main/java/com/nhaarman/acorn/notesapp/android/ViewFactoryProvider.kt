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

package com.nhaarman.acorn.notesapp.android

import com.nhaarman.acorn.android.presentation.bindViews
import com.nhaarman.acorn.notesapp.android.ui.createitem.CreateItemViewController
import com.nhaarman.acorn.notesapp.android.ui.edititem.EditItemViewController
import com.nhaarman.acorn.notesapp.android.ui.itemlist.ItemListViewController
import com.nhaarman.acorn.notesapp.presentation.createitem.CreateItemScene
import com.nhaarman.acorn.notesapp.presentation.edititem.EditItemScene
import com.nhaarman.acorn.notesapp.presentation.itemlist.ItemListScene

object ViewFactoryProvider {

    val viewFactory by lazy {
        bindViews {

            bind(
                ItemListScene.key,
                R.layout.itemlist_scene,
                ::ItemListViewController
            )

            bind(
                CreateItemScene.key,
                R.layout.createitem_scene,
                ::CreateItemViewController
            )

            bind(
                EditItemScene.key,
                R.layout.edititem_scene,
                ::EditItemViewController
            )
        }
    }
}