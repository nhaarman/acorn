package com.nhaarman.bravo.notesapp.android.ui.itemlist

import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.nhaarman.bravo.android.presentation.RestorableLayoutContainer
import com.nhaarman.bravo.notesapp.note.NoteItem
import com.nhaarman.bravo.notesapp.presentation.itemlist.ItemListContainer
import io.reactivex.Observable
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.itemlist_scene.*

class ItemListView(
    override val containerView: ViewGroup
) : ItemListContainer, LayoutContainer, RestorableLayoutContainer {

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