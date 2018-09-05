package com.nhaarman.bravo.notesapp.android

import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.android.navigation.AbstractNavigatorProvider
import com.nhaarman.bravo.notesapp.NotesAppComponent
import com.nhaarman.bravo.notesapp.navigation.NotesAppNavigator

class NotesAppNavigatorProvider(
    private val notesAppComponent: NotesAppComponent
) : AbstractNavigatorProvider<NotesAppNavigator>() {

    override fun createNavigator(savedState: NavigatorState?): NotesAppNavigator {
        return NotesAppNavigator(notesAppComponent, savedState)
    }
}