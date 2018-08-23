package com.nhaarman.bravo.notesapp.presentation.createitem

import com.nhaarman.bravo.SceneState
import com.nhaarman.bravo.notesapp.mainThread
import com.nhaarman.bravo.notesapp.note.NoteItem
import com.nhaarman.bravo.notesapp.note.NoteItemsRepository
import com.nhaarman.bravo.presentation.RxScene
import com.nhaarman.bravo.presentation.SceneKey.Companion.defaultKey
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom

class CreateItemScene(
    private val initialText: String?,
    private val noteItemsRepository: NoteItemsRepository,
    private val listener: Events,
    savedState: SceneState? = null
) : RxScene<CreateItemContainer>(savedState) {

    private val textObservable by lazy {
        Observable.just(initialText ?: "")
            .concatWith(view.whenAvailable { it.textChanges })
            .replay(1).autoConnect(this)
    }

    private val createClicks = view.whenAvailable { it.createClicks }

    override fun onStart() {
        disposables += createClicks
            .withLatestFrom(textObservable) { _, text -> text }
            .firstElement()
            .flatMapSingle { text -> noteItemsRepository.create(text) }
            .observeOn(mainThread)
            .subscribe { noteItem -> listener.created(noteItem) }
    }

    override fun attach(v: CreateItemContainer) {
        super.attach(v)
        v.setInitialText(initialText)
    }

    override fun toString(): String {
        return "CreateItemScene@${Integer.toHexString(hashCode())}"
    }

    interface Events {

        fun created(noteItem: NoteItem)
    }

    companion object {

        val key = defaultKey<CreateItemScene>()
    }
}