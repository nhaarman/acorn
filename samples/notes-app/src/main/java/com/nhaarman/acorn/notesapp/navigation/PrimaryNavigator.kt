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

import com.nhaarman.acorn.navigation.StackNavigator
import com.nhaarman.acorn.notesapp.NotesAppComponent
import com.nhaarman.acorn.notesapp.note.NoteItem
import com.nhaarman.acorn.notesapp.presentation.edititem.EditItemScene
import com.nhaarman.acorn.notesapp.presentation.itemlist.ItemListScene
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.SceneState
import kotlin.reflect.KClass

/**
 * A Navigator that starts at the [ItemListScene], and can navigate to
 * [EditItemScene]s.
 */
class PrimaryNavigator(
    private val notesAppComponent: NotesAppComponent,
    private val listener: Events,
    savedState: NavigatorState?
) : StackNavigator(savedState),
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
        listener.createItemRequested()
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

    override fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<out Container> {
        return when (sceneClass) {
            ItemListScene::class -> ItemListScene(
                notesAppComponent.noteItemsRepository,
                this,
                state
            )
            EditItemScene::class -> EditItemScene.create(
                notesAppComponent.noteItemsRepository,
                this,
                state!!
            )
            else -> error("Unknown scene: $sceneClass")
        }
    }

    interface Events {

        fun createItemRequested()
    }
}