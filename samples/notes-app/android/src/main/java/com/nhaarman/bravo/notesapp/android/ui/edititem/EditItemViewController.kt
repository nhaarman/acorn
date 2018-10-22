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

package com.nhaarman.bravo.notesapp.android.ui.edititem

import android.view.View
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import com.jakewharton.rxbinding2.widget.textChanges
import com.nhaarman.bravo.android.presentation.RestorableViewController
import com.nhaarman.bravo.notesapp.android.R
import com.nhaarman.bravo.notesapp.presentation.edititem.EditItemContainer
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
        RxToolbar.itemClicks(editItemToolbar)
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