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

package com.nhaarman.acorn.notesapp.android.ui.edititem

import android.view.View
import com.jakewharton.rxbinding3.appcompat.itemClicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.notesapp.android.R
import com.nhaarman.acorn.notesapp.presentation.edititem.EditItemContainer
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import kotlinx.android.synthetic.main.edititem_scene.*

class EditItemViewController(
    override val view: View
) : EditItemContainer, RestorableViewController {

    override var initialText: String? = null
        set(value) {
            if (editText.text.isNotBlank()) return

            editText.setText(value)
            editText.setSelection(value?.length ?: 0)
        }

    private val menuClicks by lazy {
        editItemToolbar.itemClicks()
            .share()
    }

    override val saveClicks: Observable<String> by lazy {
        menuClicks
            .filter { it.itemId == R.id.save }
            .withLatestFrom(editText.textChanges()) { _, text -> text.toString() }
            .share()
    }

    override val deleteClicks: Observable<Unit> by lazy {
        menuClicks
            .filter { it.itemId == R.id.delete }
            .map { Unit }
            .share()
    }
}