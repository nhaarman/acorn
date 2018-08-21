package com.nhaarman.bravo.notesapp.android.ui.createitem

import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import com.jakewharton.rxbinding2.widget.textChanges
import com.nhaarman.bravo.BravoBundle
import com.nhaarman.bravo.android.presentation.RestorableLayoutContainer
import com.nhaarman.bravo.notesapp.android.R
import com.nhaarman.bravo.notesapp.presentation.createitem.CreateItemContainer
import io.reactivex.Observable
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.createitem_scene.*

class CreateItemView(
    override val containerView: ViewGroup
) : CreateItemContainer, LayoutContainer, RestorableLayoutContainer {

    private var stateRestored = false

    override fun setInitialText(text: String?) {
        if (!stateRestored) {
            editText.setText(text)
            editText.setSelection(text?.length ?: 0)
        }
    }

    private val menuClicks by lazy {
        RxToolbar.itemClicks(createItemToolbar)
            .share()
    }

    override val textChanges: Observable<String>by lazy {
        editText.textChanges().map { it.toString() }
    }

    override val createClicks: Observable<Unit> by lazy {
        menuClicks
            .filter { it.itemId == R.id.save }
            .map { Unit }
            .share()
    }

    override fun restoreInstanceState(bundle: BravoBundle) {
        super.restoreInstanceState(bundle)
        stateRestored = true
    }

    override fun toString(): String {
        return "CreateItemView@${Integer.toHexString(hashCode())}"
    }
}