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