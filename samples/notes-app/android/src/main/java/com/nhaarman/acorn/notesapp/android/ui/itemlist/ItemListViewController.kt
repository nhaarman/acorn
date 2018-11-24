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

package com.nhaarman.acorn.notesapp.android.ui.itemlist

import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.notesapp.note.NoteItem
import com.nhaarman.acorn.notesapp.presentation.itemlist.ItemListContainer
import io.reactivex.Observable
import kotlinx.android.synthetic.main.itemlist_scene.*

class ItemListViewController(
    override val view: View
) : ItemListContainer, RestorableViewController {

    override var items: List<NoteItem> = emptyList()
        set(value) {
            itemsRecyclerView.items = value
        }

    override val createClicks: Observable<Unit> by lazy {
        createButton.clicks()
    }

    override val itemClicks: Observable<NoteItem> by lazy {
        Observable
            .create<NoteItem> { emitter ->
                val listener = object : ItemsRecyclerView.ClicksListener {

                    override fun onItemClicked(item: NoteItem) {
                        emitter.onNext(item)
                    }

                    override fun onDeleteClicked(item: NoteItem) {
                    }
                }
                itemsRecyclerView.addClicksListener(listener)
                emitter.setCancellable { itemsRecyclerView.removeClicksListener(listener) }
            }
            .share()
    }

    override val deleteClicks: Observable<NoteItem> by lazy {
        Observable
            .create<NoteItem> { emitter ->
                val listener = object : ItemsRecyclerView.ClicksListener {

                    override fun onItemClicked(item: NoteItem) {
                    }

                    override fun onDeleteClicked(item: NoteItem) {
                        emitter.onNext(item)
                    }
                }
                itemsRecyclerView.addClicksListener(listener)
                emitter.setCancellable { itemsRecyclerView.removeClicksListener(listener) }
            }
            .share()
    }
}