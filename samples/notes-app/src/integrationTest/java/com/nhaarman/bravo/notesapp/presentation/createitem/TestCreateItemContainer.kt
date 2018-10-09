package com.nhaarman.bravo.notesapp.presentation.createitem

import com.nhaarman.bravo.state.ContainerState
import io.reactivex.subjects.PublishSubject

class TestCreateItemContainer : CreateItemContainer {

    override fun setInitialText(text: String?) {
    }

    override val textChanges = PublishSubject.create<String>()

    override val createClicks = PublishSubject.create<Unit>()

    override fun saveInstanceState(): ContainerState {
        return ContainerState()
    }

    override fun restoreInstanceState(bundle: ContainerState) {
    }
}