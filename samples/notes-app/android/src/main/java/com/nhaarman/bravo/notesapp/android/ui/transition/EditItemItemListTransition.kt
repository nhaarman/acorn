/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.notesapp.android.ui.transition

import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import com.nhaarman.bravo.android.transition.FadeOutToBottomTransition
import com.nhaarman.bravo.android.transition.Transition
import com.nhaarman.bravo.android.util.inflate
import com.nhaarman.bravo.notesapp.android.R
import com.nhaarman.bravo.notesapp.android.ui.itemlist.ItemListViewController
import kotlinx.android.synthetic.main.edititem_scene.view.*

/**
 * Shows a 'shared element transition' that reverses the transition animation
 * as defined in [ItemListEditItemTransition].
 *
 * This uses the [View.tag] property to temporarily store coordinate information.
 */
object EditItemItemListTransition : Transition {

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        val editItemLayout = parent.getChildAt(0)
        val clickedItemViewData = editItemLayout.tag as? ClickedItemViewData

        if (clickedItemViewData == null) {
            FadeOutToBottomTransition {
                val view = parent.inflate<ConstraintLayout>(R.layout.itemlist_scene)
                ItemListViewController(view)
            }.execute(parent, callback)
            return
        }

        val itemListLayout = parent.inflate<ConstraintLayout>(R.layout.itemlist_scene)
        parent.addView(itemListLayout, 0)

        val viewController = ItemListViewController(parent)
        callback.attach(viewController)

        val shortAnimationDuration = parent.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        val editItemToolbar = editItemLayout.editItemToolbar
        val scrollView = editItemLayout.scrollView
        scrollView.setBackgroundColor(Color.WHITE)

        editItemLayout.editText.visibility = View.INVISIBLE

        parent.doOnPreDraw {
            editItemToolbar.animate()
                .translationY((-editItemToolbar.height).toFloat())
                .setDuration(shortAnimationDuration)

            val scrollViewY = IntArray(2).also { scrollView.getLocationInWindow(it) }[1]
            scrollView.pivotY = 0f

            scrollView.animate()
                .scaleY((clickedItemViewData.clickedItemHeight / scrollView.height.toFloat()))
                .translationY(-(scrollViewY - clickedItemViewData.clickedItemY).toFloat())
                .translationZ(10f)
                .setDuration(shortAnimationDuration)
                .withEndAction {
                    scrollView.animate()
                        .translationZ(0f)
                        .setDuration(shortAnimationDuration)
                        .withEndAction {
                            parent.removeView(editItemLayout)
                            callback.onComplete(viewController)
                        }
                }
        }
    }
}