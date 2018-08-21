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