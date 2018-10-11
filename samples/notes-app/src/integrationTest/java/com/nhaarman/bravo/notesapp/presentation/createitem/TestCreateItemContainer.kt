package com.nhaarman.bravo.notesapp.presentation.createitem

import com.nhaarman.bravo.testing.RestorableTestContainer
import io.reactivex.subjects.PublishSubject

class TestCreateItemContainer : CreateItemContainer, RestorableTestContainer {

    override fun setInitialText(text: String?) {
    }

    override val textChanges = PublishSubject.create<String>()

    override val createClicks = PublishSubject.create<Unit>()
}