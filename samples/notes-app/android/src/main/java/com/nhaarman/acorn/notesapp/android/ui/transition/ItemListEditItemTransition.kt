/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nhaarman.acorn.notesapp.android.ui.transition

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnPreDraw
import com.nhaarman.acorn.android.transition.FadeInFromBottomTransition
import com.nhaarman.acorn.android.transition.SceneTransition
import com.nhaarman.acorn.android.util.inflate
import com.nhaarman.acorn.notesapp.android.R
import com.nhaarman.acorn.notesapp.android.ui.edititem.EditItemViewController
import com.nhaarman.acorn.notesapp.android.ui.itemlist.ItemsRecyclerView

/**
 * Shows a 'shared element transition' that originates from the clicked view.
 */
object ItemListEditItemTransition : SceneTransition {

    override fun execute(parent: ViewGroup, callback: SceneTransition.Callback) {
        val itemListLayout = parent.getChildAt(0)
        val itemsRecyclerView = itemListLayout.findViewById<ItemsRecyclerView>(R.id.itemsRecyclerView)
        val clickedView = itemsRecyclerView.clickedView
        if (clickedView == null) {
            FadeInFromBottomTransition {
                EditItemViewController(parent.inflate(R.layout.createitem_scene))
            }.execute(parent, callback)
            return
        }

        val editItemLayout = parent.inflate<ConstraintLayout>(R.layout.edititem_scene)
        parent.addView(editItemLayout)

        val viewController = EditItemViewController(parent)
        callback.attach(viewController)

        parent.doOnPreDraw {
            val shortAnimationDuration = parent.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            val editItemToolbar = editItemLayout.findViewById<View>(R.id.editItemToolbar)
            editItemToolbar.translationY = (-editItemToolbar.height).toFloat()

            val scrollView = editItemLayout.findViewById<View>(R.id.scrollView)
            scrollView.setBackgroundColor(Color.WHITE)

            val clickedViewHeight = clickedView.height
            val scrollViewHeight = scrollView.height
            scrollView.scaleY = clickedViewHeight / scrollViewHeight.toFloat()

            val clickedViewY = IntArray(2).also { clickedView.getLocationInWindow(it) }[1]
            val scrollViewY = IntArray(2).also { scrollView.getLocationInWindow(it) }[1]
            scrollView.translationY = ((clickedViewY - scrollViewY).toFloat())

            val editItemET = editItemLayout.findViewById<View>(R.id.editText)
            editItemET.visibility = View.INVISIBLE

            val createButton = itemListLayout.findViewById<View>(R.id.createButton)
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
                            callback.onComplete(viewController)
                        }
                }
        }
    }
}
