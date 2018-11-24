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

package com.nhaarman.bravo.notesapp.android.ui.createitem

import android.view.View
import com.jakewharton.rxbinding3.appcompat.itemClicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.nhaarman.bravo.android.presentation.RestorableViewController
import com.nhaarman.bravo.notesapp.android.R
import com.nhaarman.bravo.notesapp.presentation.createitem.CreateItemContainer
import com.nhaarman.bravo.state.ContainerState
import io.reactivex.Observable
import kotlinx.android.synthetic.main.createitem_scene.*

class CreateItemViewController(
    override val view: View
) : CreateItemContainer, RestorableViewController {

    private var stateRestored = false

    override fun setInitialText(text: String?) {
        if (!stateRestored) {
            editText.setText(text)
            editText.setSelection(text?.length ?: 0)
        }
    }

    private val menuClicks by lazy {
        createItemToolbar.itemClicks()
            .share()
    }

    override val textChanges: Observable<String>by lazy {
        editText.textChanges().map { it.toString() }
    }

    override val createClicks: Observable<Unit> by lazy {
        menuClicks
            .filter { it.itemId == R.id.save }
            .map { Unit }
            .share()
    }

    override fun restoreInstanceState(bundle: ContainerState) {
        super.restoreInstanceState(bundle)
        stateRestored = true
    }
}