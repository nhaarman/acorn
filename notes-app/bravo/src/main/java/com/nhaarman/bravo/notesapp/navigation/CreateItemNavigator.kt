package com.nhaarman.bravo.notesapp.navigation

import com.nhaarman.bravo.NavigatorState
import com.nhaarman.bravo.SceneState
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.navigation.SingleSceneNavigator
import com.nhaarman.bravo.notesapp.NotesAppComponent
import com.nhaarman.bravo.notesapp.note.NoteItem
import com.nhaarman.bravo.notesapp.presentation.createitem.CreateItemScene
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

class CreateItemNavigator(
    private val text: String?,
    private val notesAppComponent: NotesAppComponent,
    private val savedState: NavigatorState?,
    private val listener: Events
) : SingleSceneNavigator<CreateItemNavigator.Events>(savedState),
    CreateItemScene.Events {

    override fun createScene(state: SceneState?): Scene<out Container> {
        return CreateItemScene(
            text,
            notesAppComponent.noteItemsRepository,
            this,
            state
        )
    }

    override fun created(noteItem: NoteItem) {
        listener.created(noteItem)
    }

    interface Events : Navigator.Events {

        fun created(noteItem: NoteItem)
    }
}
