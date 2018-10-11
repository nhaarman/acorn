package com.nhaarman.bravo.notesapp.presentation.edititem

import com.nhaarman.bravo.testing.RestorableTestContainer
import io.reactivex.subjects.PublishSubject

class TestEditItemContainer : EditItemContainer, RestorableTestContainer {

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
}