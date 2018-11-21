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