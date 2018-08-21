package com.nhaarman.bravo.notesapp.presentation.createitem

import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.StateRestorable
import io.reactivex.Observable

interface CreateItemContainer : Container, StateRestorable {

    fun setInitialText(text: String?)

    val textChanges: Observable<String>
    val createClicks: Observable<Unit>
}