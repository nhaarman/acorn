package com.nhaarman.bravo.notesapp.presentation.edititem

import com.nhaarman.bravo.SceneState
import com.nhaarman.bravo.notesapp.mainThread
import com.nhaarman.bravo.notesapp.note.NoteItemsRepository
import com.nhaarman.bravo.presentation.RxScene
import io.reactivex.rxkotlin.plusAssign

class EditItemScene(
    private val itemId: Long,
    private val noteItemsRepository: NoteItemsRepository,
    private val listener: Events,
    savedState: SceneState? = null
) : RxScene<EditItemContainer>(savedState) {

    override val key = EditItemScene.key

    private val originalItem by lazy {
        noteItemsRepository.find(itemId)
            .observeOn(mainThread)
            .replay(1).autoConnect(this)
    }

    private val saveClicks = view.whenAvailable { it.saveClicks }
    private val deleteClicks = view.whenAvailable { it.deleteClicks }

    override fun onStart() {
        disposables += originalItem
            .combineWithLatestView()
            .firstElement()
            .subscribe { (item, view) ->
                view?.initialText = item.orNull()?.text
            }

        disposables += saveClicks
            .flatMapSingle { text -> noteItemsRepository.update(itemId, text) }
            .observeOn(mainThread)
            .subscribe { _ -> listener.saved() }

        disposables += deleteClicks
            .firstElement()
            .subscribe {
                noteItemsRepository.delete(itemId)
                listener.deleted()
            }
    }

    override fun saveInstanceState(): SceneState {
        return super.saveInstanceState()
            .also { it["item_id"] = itemId }
    }

    override fun toString(): String {
        return "EditItemScene(itemId=$itemId)@${Integer.toHexString(hashCode())}"
    }

    interface Events {

        fun saved()
        fun deleted()
    }

    companion object {

        val key: String = EditItemScene::class.java.name

        fun create(
            noteItemsRepository: NoteItemsRepository,
            listener: Events,
            state: SceneState
        ): EditItemScene {
            return EditItemScene(
                state["item_id"]!!,
                noteItemsRepository,
                listener,
                state
            )
        }
    }
}