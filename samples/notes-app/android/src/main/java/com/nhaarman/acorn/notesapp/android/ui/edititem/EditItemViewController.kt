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

package com.nhaarman.acorn.notesapp.android.ui.edititem

import android.view.View
import com.jakewharton.rxbinding3.appcompat.itemClicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.notesapp.android.R
import com.nhaarman.acorn.notesapp.presentation.edititem.EditItemContainer
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.edititem_scene.*

class EditItemViewController(
    override val view: View
) : EditItemContainer, RestorableViewController, LayoutContainer {

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

    override val containerView = view
}
