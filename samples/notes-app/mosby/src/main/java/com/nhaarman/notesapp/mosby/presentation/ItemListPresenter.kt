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

package com.nhaarman.notesapp.mosby.presentation

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.nhaarman.notesapp.mosby.note.NoteItemsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class ItemListPresenter(
    private val noteItemsRepository: NoteItemsRepository
) : MvpBasePresenter<ItemListView>() {

    private val items = noteItemsRepository.noteItems
        .observeOn(AndroidSchedulers.mainThread())
        .replay(1).autoConnect(0)

    fun loadItems(): Disposable {
        return items
            .subscribe { items ->
                ifViewAttached { it.items = items }
            }
    }
}