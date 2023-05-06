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

import com.nhaarman.acorn.navigation.CompositeStackNavigator
import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.notesapp.NotesAppComponent
import com.nhaarman.acorn.notesapp.note.NoteItem
import com.nhaarman.acorn.state.NavigatorState
import kotlin.reflect.KClass

class NotesAppNavigator(
    private val notesAppComponent: NotesAppComponent,
    savedState: NavigatorState?,
) : CompositeStackNavigator(savedState),
    PrimaryNavigator.Events,
    CreateItemNavigator.Events {

    override fun initialStack(): List<Navigator> {
        return listOf(PrimaryNavigator(notesAppComponent, this, null))
    }

    override fun instantiateNavigator(
        navigatorClass: KClass<out Navigator>,
        state: NavigatorState?,
    ): Navigator {
        return when (navigatorClass) {
            PrimaryNavigator::class -> PrimaryNavigator(
                notesAppComponent,
                this,
                state,
            )
            CreateItemNavigator::class -> CreateItemNavigator(
                null,
                notesAppComponent,
                state,
                this,
            )
            else -> error("Could not instantiate navigator for class $navigatorClass.")
        }
    }

    override fun createItemRequested() {
        push(CreateItemNavigator(null, notesAppComponent, null, this))
    }

    override fun created(noteItem: NoteItem) {
        pop()
    }
}
