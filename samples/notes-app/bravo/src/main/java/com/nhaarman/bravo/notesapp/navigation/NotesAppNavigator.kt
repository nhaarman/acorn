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
import com.nhaarman.bravo.navigation.CompositeStackNavigator
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.notesapp.NotesAppComponent
import com.nhaarman.bravo.notesapp.note.NoteItem

class NotesAppNavigator(
    private val notesAppComponent: NotesAppComponent,
    savedState: NavigatorState?
) : CompositeStackNavigator<Navigator.Events>(savedState),
    PrimaryNavigator.Events,
    CreateItemNavigator.Events {

    override fun initialStack(): List<Navigator<out Navigator.Events>> {
        return listOf(PrimaryNavigator(notesAppComponent, null))
    }

    override fun instantiateNavigator(
        navigatorClass: Class<Navigator<*>>,
        state: NavigatorState?
    ): Navigator<out Navigator.Events> {
        return when (navigatorClass) {
            PrimaryNavigator::class.java -> PrimaryNavigator(
                notesAppComponent,
                state
            )
            CreateItemNavigator::class.java -> CreateItemNavigator(
                null,
                notesAppComponent,
                state,
                this
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