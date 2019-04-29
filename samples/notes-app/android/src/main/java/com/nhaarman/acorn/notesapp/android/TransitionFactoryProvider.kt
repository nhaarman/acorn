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

import com.nhaarman.acorn.android.transition.FadeOutToBottomTransition
import com.nhaarman.acorn.android.transition.SceneTransitionFactory
import com.nhaarman.acorn.android.transition.hideKeyboardOnStart
import com.nhaarman.acorn.android.transition.sceneTransitionFactory
import com.nhaarman.acorn.notesapp.android.ui.transition.EditItemItemListTransition
import com.nhaarman.acorn.notesapp.android.ui.transition.ItemListCreateItemTransition
import com.nhaarman.acorn.notesapp.android.ui.transition.ItemListEditItemTransition
import com.nhaarman.acorn.notesapp.presentation.createitem.CreateItemScene
import com.nhaarman.acorn.notesapp.presentation.edititem.EditItemScene
import com.nhaarman.acorn.notesapp.presentation.itemlist.ItemListScene

object TransitionFactoryProvider {

    val transitionFactory: SceneTransitionFactory by lazy {
        sceneTransitionFactory(ViewFactoryProvider.viewFactory) {
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
