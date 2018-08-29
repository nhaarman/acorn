package com.nhaarman.bravo.notesapp.android.ui.edititem

import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import com.jakewharton.rxbinding2.widget.textChanges
import com.nhaarman.bravo.android.presentation.RestorableLayoutContainer
import com.nhaarman.bravo.notesapp.android.R
import com.nhaarman.bravo.notesapp.presentation.edititem.EditItemContainer
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.edititem_scene.*

class EditItemView(
    override val containerView: ViewGroup
) : EditItemContainer, LayoutContainer, RestorableLayoutContainer {

    override var initialText: String? = null
        set(value) {
            if (editText.text.isNotBlank()) return

            editText.setText(value)
            editText.setSelection(value?.length ?: 0)
        }

    private val menuClicks by lazy {
        RxToolbar.itemClicks(editItemToolbar)
            .share()
    }

    override val saveClicks: Observable<String> by lazy {
        menuClicks
            .filter { it.itemId == R.id.save }
            .withLatestFrom(editText.textChanges()) { _, text -> text.toString() }
            .share()
    }

    override val deleteClicks: Observable<Unit> by lazy {
        menuClicks
            .filter { it.itemId == R.id.delete }
            .map { Unit }
            .share()
    }

    override fun toString(): String {
        return "CreateItemView@${Integer.toHexString(hashCode())}"
    }
}