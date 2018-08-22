package com.nhaarman.bravo.notesapp.presentation.createitem

import com.nhaarman.bravo.presentation.RestorableContainer
import com.nhaarman.bravo.presentation.Container
import io.reactivex.Observable

interface CreateItemContainer : Container, RestorableContainer {

    fun setInitialText(text: String?)

    val textChanges: Observable<String>
    val createClicks: Observable<Unit>
}