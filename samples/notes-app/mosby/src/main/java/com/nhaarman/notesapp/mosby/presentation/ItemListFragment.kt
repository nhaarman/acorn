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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvp.viewstate.MvpViewStateFragment
import com.nhaarman.notesapp.mosby.R
import com.nhaarman.notesapp.mosby.note.NoteItem
import com.nhaarman.notesapp.mosby.noteAppComponent
import kotlinx.android.synthetic.main.fragment_itemlist.*

/**
 * List view loses scroll state on rotation.
 * It is not clear how Mosby would support this.
 */
class ItemListFragment : MvpViewStateFragment<ItemListView, ItemListPresenter, ItemListViewState>(), ItemListView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_itemlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadItems()
    }

    override fun createPresenter(): ItemListPresenter {
        return ItemListPresenter(requireActivity().noteAppComponent.noteItemsRepository)
    }

    override fun createViewState(): ItemListViewState {
        return ItemListViewState()
    }

    override fun onNewViewStateInstance() {
    }

    override var items: List<NoteItem> = emptyList()
        set(value) {
            viewState.items = value
            itemsRecyclerView.items = value
        }
}