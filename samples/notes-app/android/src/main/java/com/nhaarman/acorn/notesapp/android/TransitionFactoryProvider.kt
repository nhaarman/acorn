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

import com.nhaarman.acorn.android.transition.FadeOutToBottomTransition
import com.nhaarman.acorn.android.transition.TransitionFactory
import com.nhaarman.acorn.android.transition.hideKeyboardOnStart
import com.nhaarman.acorn.android.transition.transitionFactory
import com.nhaarman.acorn.notesapp.android.ui.transition.EditItemItemListTransition
import com.nhaarman.acorn.notesapp.android.ui.transition.ItemListCreateItemTransition
import com.nhaarman.acorn.notesapp.android.ui.transition.ItemListEditItemTransition
import com.nhaarman.acorn.notesapp.presentation.createitem.CreateItemScene
import com.nhaarman.acorn.notesapp.presentation.edititem.EditItemScene
import com.nhaarman.acorn.notesapp.presentation.itemlist.ItemListScene

object TransitionFactoryProvider {

    val transitionFactory: TransitionFactory by lazy {
        transitionFactory(ViewFactoryProvider.viewFactory) {
            (ItemListScene::class to CreateItemScene::class) use ItemListCreateItemTransition

            (ItemListScene::class to EditItemScene::class) use ItemListEditItemTransition
            (EditItemScene::class to ItemListScene::class) use EditItemItemListTransition

            val fadeOutToBottom =
                FadeOutToBottomTransition.from(ViewFactoryProvider.viewFactory)
                    .hideKeyboardOnStart()

            (CreateItemScene::class to ItemListScene::class) use fadeOutToBottom
            (EditItemScene::class to ItemListScene::class) use fadeOutToBottom
        }
    }
}