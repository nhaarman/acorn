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

package com.nhaarman.acorn.notesapp.integration.presentation.edititem

import com.nhaarman.acorn.notesapp.presentation.edititem.EditItemContainer
import com.nhaarman.acorn.testing.RestorableTestContainer
import io.reactivex.subjects.PublishSubject

class TestEditItemContainer : EditItemContainer, RestorableTestContainer {

    override var initialText: String? = null
        set(value) {
            if (text == "") text = value ?: ""
        }

    override val saveClicks = PublishSubject.create<String>()
    override val deleteClicks = PublishSubject.create<Unit>()

    private var text = ""
    fun enterText(text: String) {
        this.text = text
    }

    fun clickSave() {
        saveClicks.onNext(text)
    }
}