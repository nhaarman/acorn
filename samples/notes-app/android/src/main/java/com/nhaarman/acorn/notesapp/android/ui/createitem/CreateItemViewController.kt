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

package com.nhaarman.acorn.notesapp.android.ui.createitem

import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.jakewharton.rxbinding3.appcompat.itemClicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.notesapp.android.R
import com.nhaarman.acorn.notesapp.presentation.createitem.CreateItemContainer
import com.nhaarman.acorn.state.ContainerState
import io.reactivex.Observable

class CreateItemViewController(
    override val view: View,
) : CreateItemContainer, RestorableViewController {

    private var stateRestored = false

    override fun setInitialText(text: String?) {
        if (!stateRestored) {
            view.findViewById<EditText>(R.id.editText).setText(text)
            view.findViewById<EditText>(R.id.editText).setSelection(text?.length ?: 0)
        }
    }

    private val menuClicks by lazy {
        view.findViewById<Toolbar>(R.id.createItemToolbar).itemClicks()
            .share()
    }

    override val textChanges: Observable<String> by lazy {
        view.findViewById<EditText>(R.id.editText).textChanges().map { it.toString() }
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
