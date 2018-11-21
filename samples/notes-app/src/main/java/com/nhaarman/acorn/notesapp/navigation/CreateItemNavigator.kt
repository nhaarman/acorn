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

package com.nhaarman.acorn.notesapp.navigation

import com.nhaarman.acorn.navigation.SingleSceneNavigator
import com.nhaarman.acorn.notesapp.NotesAppComponent
import com.nhaarman.acorn.notesapp.note.NoteItem
import com.nhaarman.acorn.notesapp.presentation.createitem.CreateItemScene
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.SceneState

class CreateItemNavigator(
    private val text: String?,
    private val notesAppComponent: NotesAppComponent,
    private val savedState: NavigatorState?,
    private val listener: Events
) : SingleSceneNavigator(savedState),
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

    interface Events {

        fun created(noteItem: NoteItem)
    }
}
