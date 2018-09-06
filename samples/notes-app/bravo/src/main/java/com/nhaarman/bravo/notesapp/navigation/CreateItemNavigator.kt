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

package com.nhaarman.bravo.notesapp.navigation

import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.state.SceneState
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
