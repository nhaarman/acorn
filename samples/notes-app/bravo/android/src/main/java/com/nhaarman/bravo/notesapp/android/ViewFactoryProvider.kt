package com.nhaarman.bravo.notesapp.android

import com.nhaarman.bravo.android.presentation.bindViews
import com.nhaarman.bravo.notesapp.android.ui.createitem.CreateItemView
import com.nhaarman.bravo.notesapp.android.ui.edititem.EditItemView
import com.nhaarman.bravo.notesapp.android.ui.itemlist.ItemListView
import com.nhaarman.bravo.notesapp.presentation.createitem.CreateItemScene
import com.nhaarman.bravo.notesapp.presentation.edititem.EditItemScene
import com.nhaarman.bravo.notesapp.presentation.itemlist.ItemListScene

object ViewFactoryProvider {

    val viewFactory by lazy {
        bindViews {

            bindViewGroup(
                ItemListScene.key,
                R.layout.itemlist_scene
            ) {
                ItemListView(it)
            }

            bindViewGroup(
                CreateItemScene.key,
                R.layout.createitem_scene
            ) {
                CreateItemView(it)
            }

            bindViewGroup(
                EditItemScene.key,
                R.layout.edititem_scene
            ) {
                EditItemView(it)
            }
        }
    }
}