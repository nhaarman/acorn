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
import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import com.nhaarman.bravo.android.presentation.ViewResult
import com.nhaarman.bravo.android.transition.FadeInFromBottomTransition
import com.nhaarman.bravo.android.transition.Transition
import com.nhaarman.bravo.android.util.inflate
import com.nhaarman.bravo.notesapp.android.R
import com.nhaarman.bravo.notesapp.android.ui.edititem.EditItemView
import kotlinx.android.synthetic.main.edititem_scene.view.*
import kotlinx.android.synthetic.main.itemlist_scene.view.*

/**
 * Shows a 'shared element transition' that originates from the clicked view.
 */
object ItemListEditItemTransition : Transition {

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        val itemListLayout = parent.getChildAt(0)
        val itemsRecyclerView = itemListLayout.itemsRecyclerView
        val clickedView = itemsRecyclerView.clickedView
        if (clickedView == null) {
            FadeInFromBottomTransition { _ ->
                val view = parent.inflate<ConstraintLayout>(R.layout.edititem_scene)
                ViewResult.from(view, EditItemView(view))
            }.execute(parent, callback)
            return
        }

        val editItemLayout = parent.inflate<ConstraintLayout>(R.layout.edititem_scene)
        parent.addView(editItemLayout)

        val viewResult = ViewResult.from(parent, EditItemView(parent))
        callback.attach(viewResult)

        parent.doOnPreDraw { _ ->
            val shortAnimationDuration = parent.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            val editItemToolbar = editItemLayout.editItemToolbar
            editItemToolbar.translationY = (-editItemToolbar.height).toFloat()

            val scrollView = editItemLayout.scrollView
            scrollView.setBackgroundColor(Color.WHITE)

            val clickedViewHeight = clickedView.height
            val scrollViewHeight = scrollView.height
            scrollView.scaleY = clickedViewHeight / scrollViewHeight.toFloat()

            val clickedViewY = IntArray(2).also { clickedView.getLocationInWindow(it) }[1]
            val scrollViewY = IntArray(2).also { scrollView.getLocationInWindow(it) }[1]
            scrollView.translationY = ((clickedViewY - scrollViewY).toFloat())

            val editItemET = editItemLayout.editText
            editItemET.visibility = View.INVISIBLE

            val createButton = itemListLayout.createButton
            createButton.animate()
                .translationY(createButton.height.toFloat() * 2)
                .setDuration(shortAnimationDuration)

            scrollView.animate()
                .translationZ(10f)
                .setDuration(shortAnimationDuration)
                .withEndAction {
                    editItemToolbar.animate()
                        .translationY(0f)
                        .setDuration(shortAnimationDuration)

                    scrollView.animate()
                        .scaleY(1f)
                        .translationY(0f)
                        .translationZ(0f)
                        .withEndAction {
                            editItemLayout.tag = ClickedItemViewData(clickedViewHeight, clickedViewY)

                            scrollView.background = null
                            editItemET.visibility = View.VISIBLE
                            parent.removeView(itemListLayout)
                            callback.onComplete(viewResult)
                        }
                }
        }
    }
}