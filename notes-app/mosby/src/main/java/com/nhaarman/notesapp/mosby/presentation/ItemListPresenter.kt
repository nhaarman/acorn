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