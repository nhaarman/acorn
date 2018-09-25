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

package com.nhaarman.bravo.notesapp.android

import com.nhaarman.bravo.android.transition.FadeOutToBottomTransition
import com.nhaarman.bravo.android.transition.TransitionFactory
import com.nhaarman.bravo.android.transition.hideKeyboardOnStart
import com.nhaarman.bravo.android.transition.transitionFactory
import com.nhaarman.bravo.notesapp.android.ui.transition.EditItemItemListTransition
import com.nhaarman.bravo.notesapp.android.ui.transition.ItemListCreateItemTransition
import com.nhaarman.bravo.notesapp.android.ui.transition.ItemListEditItemTransition
import com.nhaarman.bravo.notesapp.presentation.createitem.CreateItemScene
import com.nhaarman.bravo.notesapp.presentation.edititem.EditItemScene
import com.nhaarman.bravo.notesapp.presentation.itemlist.ItemListScene

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