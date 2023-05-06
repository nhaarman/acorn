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

package com.nhaarman.acorn.notesapp.android

import com.nhaarman.acorn.android.navigation.AbstractNavigatorProvider
import com.nhaarman.acorn.notesapp.NotesAppComponent
import com.nhaarman.acorn.notesapp.navigation.NotesAppNavigator
import com.nhaarman.acorn.state.NavigatorState

class NotesAppNavigatorProvider(
    private val notesAppComponent: NotesAppComponent,
) : AbstractNavigatorProvider<NotesAppNavigator>() {

    override fun createNavigator(savedState: NavigatorState?): NotesAppNavigator {
        return NotesAppNavigator(notesAppComponent, savedState)
    }
}
