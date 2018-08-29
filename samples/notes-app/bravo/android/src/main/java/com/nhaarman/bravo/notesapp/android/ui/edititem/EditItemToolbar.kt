package com.nhaarman.bravo.notesapp.android.ui.edititem

import android.content.Context
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import com.nhaarman.bravo.notesapp.android.R

class EditItemToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = android.support.v7.appcompat.R.attr.toolbarStyle
) : Toolbar(context, attrs, defStyleAttr) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        inflateMenu(R.menu.edititem_menu)
    }
}