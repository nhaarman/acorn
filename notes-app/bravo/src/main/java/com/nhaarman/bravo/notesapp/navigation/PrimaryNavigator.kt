package com.nhaarman.bravo.notesapp.navigation

import com.nhaarman.bravo.BravoBundle
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.navigation.StackNavigator
import com.nhaarman.bravo.notesapp.note.NoteItem
import com.nhaarman.bravo.notesapp.NotesAppComponent
import com.nhaarman.bravo.notesapp.presentation.edititem.EditItemScene
import com.nhaarman.bravo.notesapp.presentation.itemlist.ItemListScene
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

/**
 * A Navigator that starts at the [ItemListScene], and can navigate to
 * [EditItemScene]s.
 *
 *
 */
class PrimaryNavigator(
    private val notesAppComponent: NotesAppComponent,
    private val savedState: BravoBundle?
) : StackNavigator<PrimaryNavigator.Events>(savedState),
    ItemListScene.Events,
    EditItemScene.Events {

    override fun initialStack(): List<Scene<out Container>> {
        return listOf(
            ItemListScene(
                noteItemsRepository = notesAppComponent.noteItemsRepository,
                listener = this,
                savedState = null
            )
        )
    }

    override fun saved() = pop()
    override fun deleted() = pop()

    override fun createItemRequested() {
        listeners.forEach { it.createItemRequested() }
    }

    override fun showItemRequested(item: NoteItem) {
        push(
            EditItemScene(
                item.id,
                notesAppComponent.noteItemsRepository,
                this
            )
        )
    }

    override fun instantiateScene(sceneClass: Class<Scene<*>>, state: BravoBundle?): Scene<out Container> {
        return when (sceneClass) {
            ItemListScene::class.java -> ItemListScene(
                notesAppComponent.noteItemsRepository,
                this,
                state
            )
            EditItemScene::class.java -> EditItemScene.create(
                notesAppComponent.noteItemsRepository,
                this,
                state!!
            )
            else -> error("Unknown scene: $sceneClass")
        }
    }

    interface Events : Navigator.Events {

        fun createItemRequested()
    }
}