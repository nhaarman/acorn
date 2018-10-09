package com.nhaarman.bravo.notesapp.presentation.edititem

import com.nhaarman.bravo.state.ContainerState
import io.reactivex.subjects.PublishSubject

class TestEditItemContainer : EditItemContainer {

    override var initialText: String? = null
        set(value) {
            if (text == "") text = value ?: ""
        }

    override val saveClicks = PublishSubject.create<String>()
    override val deleteClicks = PublishSubject.create<Unit>()

    private var text = ""
    fun enterText(text: String) {
        this.text = text
    }

    fun clickSave() {
        saveClicks.onNext(text)
    }

    override fun saveInstanceState(): ContainerState {
        return ContainerState()
    }

    override fun restoreInstanceState(bundle: ContainerState) {
    }
}