package com.nhaarman.bravo.notesapp.android

import com.nhaarman.bravo.android.transition.FadeOutToBottomTransition
import com.nhaarman.bravo.android.transition.TransitionFactory
import com.nhaarman.bravo.android.transition.hideKeyboardOnStart
import com.nhaarman.bravo.android.transition.transitionFactory
import com.nhaarman.bravo.notesapp.presentation.createitem.CreateItemScene
import com.nhaarman.bravo.notesapp.presentation.edititem.EditItemScene
import com.nhaarman.bravo.notesapp.presentation.itemlist.ItemListScene

object TransitionFactoryProvider {

    val transitionFactory: TransitionFactory by lazy {
        transitionFactory(ViewFactoryProvider.viewFactory) {
            val fadeOutToBottom =
                FadeOutToBottomTransition.from(ViewFactoryProvider.viewFactory)
                    .hideKeyboardOnStart()

            (CreateItemScene::class.java to ItemListScene::class.java) use fadeOutToBottom
            (EditItemScene::class.java to ItemListScene::class.java) use fadeOutToBottom
        }
    }
}