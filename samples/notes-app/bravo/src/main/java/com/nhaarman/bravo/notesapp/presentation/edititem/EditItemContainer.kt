package com.nhaarman.bravo.notesapp.presentation.edititem

import com.nhaarman.bravo.presentation.RestorableContainer
import com.nhaarman.bravo.presentation.Container
import io.reactivex.Observable

interface EditItemContainer : Container, RestorableContainer {

    var initialText: String?

    val saveClicks: Observable<String>
    val deleteClicks: Observable<Unit>
}