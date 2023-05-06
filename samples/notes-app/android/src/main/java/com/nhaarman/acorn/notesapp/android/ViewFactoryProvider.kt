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
                ::ItemListViewController,
            )

            bind(
                CreateItemScene.key,
                R.layout.createitem_scene,
                ::CreateItemViewController,
            )

            bind(
                EditItemScene.key,
                R.layout.edititem_scene,
                ::EditItemViewController,
            )
        }
    }
}
