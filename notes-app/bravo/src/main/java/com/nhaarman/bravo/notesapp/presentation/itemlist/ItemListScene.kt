package com.nhaarman.bravo.notesapp.presentation.itemlist

import com.nhaarman.bravo.SceneState
import com.nhaarman.bravo.notesapp.mainThread
import com.nhaarman.bravo.notesapp.note.NoteItem
import com.nhaarman.bravo.notesapp.note.NoteItemsRepository
import com.nhaarman.bravo.presentation.RxScene
import io.reactivex.rxkotlin.plusAssign

class ItemListScene(
    private val noteItemsRepository: NoteItemsRepository,
    private val listener: Events,
    savedState: SceneState?
) : RxScene<ItemListContainer>(savedState) {

    private val createClicks = view.whenAvailable { it.createClicks }
    private val itemClicks = view.whenAvailable { it.itemClicks }
    private val deleteClicks = view.whenAvailable { it.deleteClicks }

    private val items by lazy {
        noteItemsRepository.noteItems
            .observeOn(mainThread)
            .replay(1)
            .autoConnect(this)
    }

    override fun onStart() {
        disposables += items
            .combineWithLatestView()
            .subscribe { (items, view) ->
                view?.items = items
            }

        disposables += createClicks
            .subscribe { listener.createItemRequested() }

        disposables += itemClicks
            .subscribe { listener.showItemRequested(it) }

        disposables += deleteClicks
            .subscribe { noteItemsRepository.delete(it) }
    }

    override fun toString(): String {
        return "ItemListScene@${Integer.toHexString(hashCode())}"
    }

    interface Events {

        fun createItemRequested()
        fun showItemRequested(item: NoteItem)
    }

    override val key: String = ItemListScene.key

    companion object {

        val key: String = ItemListScene::class.java.name
    }
}