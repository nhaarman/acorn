package com.nhaarman.bravo.notesapp

import com.nhaarman.bravo.notesapp.presentation.createitem.CreateItemScene
import com.nhaarman.bravo.notesapp.presentation.createitem.TestCreateItemContainer
import com.nhaarman.bravo.notesapp.presentation.edititem.EditItemScene
import com.nhaarman.bravo.notesapp.presentation.edititem.TestEditItemContainer
import com.nhaarman.bravo.notesapp.presentation.itemlist.ItemListScene
import com.nhaarman.bravo.notesapp.presentation.itemlist.TestItemListContainer
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.testing.ContainerProvider

object NotesAppTestContainerProvider : ContainerProvider {

    override fun containerFor(scene: Scene<*>): Container {
        return when (scene) {
            is ItemListScene -> TestItemListContainer()
            is CreateItemScene -> TestCreateItemContainer()
            is EditItemScene -> TestEditItemContainer()
            else -> error("Unknown scene $scene")
        }
    }
}
