package com.nhaarman.notesapp.conductor.presentation.itemlist

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import kotlinx.android.synthetic.main.itemlist_itemview.view.*

class ItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    var text: String? = null
        set(value) {
            itemTextView.text = value
        }
}
