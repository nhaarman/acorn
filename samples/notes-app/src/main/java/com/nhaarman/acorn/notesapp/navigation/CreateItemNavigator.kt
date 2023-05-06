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
    private val listener: Events,
) : SingleSceneNavigator(savedState),
    CreateItemScene.Events {

    override fun createScene(state: SceneState?): Scene<out Container> {
        return CreateItemScene(
            text,
            notesAppComponent.noteItemsRepository,
            this,
            state,
        )
    }

    override fun created(noteItem: NoteItem) {
        listener.created(noteItem)
    }

    interface Events {

        fun created(noteItem: NoteItem)
    }
}
