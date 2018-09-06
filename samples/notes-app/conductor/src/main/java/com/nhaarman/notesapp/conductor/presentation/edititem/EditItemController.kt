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

package com.nhaarman.notesapp.conductor.presentation.edititem

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.rxlifecycle2.ControllerEvent
import com.bluelinelabs.conductor.rxlifecycle2.RxController
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import com.nhaarman.notesapp.conductor.R
import com.nhaarman.notesapp.conductor.noteAppComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.controller_edititem.view.*

class EditItemController(
    private val itemId: Long
) : RxController() {

    constructor(bundle: Bundle) : this(
        itemId = bundle.getLong("item_id")
    )

    private val noteItemsRepository get() = activity!!.noteAppComponent.noteItemsRepository

    private val originalItem by lazy {
        noteItemsRepository.find(itemId)
            .observeOn(AndroidSchedulers.mainThread())
            .replay(1).refCount()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_edititem, container, false)
    }

    /**
     * A flag to see if the view state is restored from a previous state.
     * If so, we should skip setting the original item text.
     */
    private var restored = false

    override fun onRestoreViewState(view: View, savedViewState: Bundle) {
        restored = true
        super.onRestoreViewState(view, savedViewState)
    }

    @SuppressLint("CheckResult")
    override fun onAttach(view: View) {
        val menuClicks = RxToolbar.itemClicks(view.editItemToolbar)
            .share()

        originalItem
            .compose(bindUntilEvent(ControllerEvent.DETACH))
            .firstElement()
            .subscribe {
                if (!restored) view.editText.setText(it.orNull()?.text)
            }

        menuClicks
            .filter { it.itemId == R.id.save }
            .map { view.editText.text.toString() }
            .compose(bindUntilEvent(ControllerEvent.DETACH))
            .flatMapSingle { text -> noteItemsRepository.update(itemId, text) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _ -> router.popCurrentController() }

        menuClicks
            .filter { it.itemId == R.id.delete }
            .compose(bindUntilEvent(ControllerEvent.DETACH))
            .subscribe {
                noteItemsRepository.delete(itemId)
                router.popCurrentController()
            }
    }
}