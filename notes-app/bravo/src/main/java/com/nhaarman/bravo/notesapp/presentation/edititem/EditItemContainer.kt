package com.nhaarman.bravo.notesapp.presentation.edititem

import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.StateRestorable
import io.reactivex.Observable

interface EditItemContainer : Container, StateRestorable {

    var initialText: String?

    val saveClicks: Observable<String>
    val deleteClicks: Observable<Unit>
}