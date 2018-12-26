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
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import com.nhaarman.acorn.android.transition.FadeOutToBottomTransition
import com.nhaarman.acorn.android.transition.Transition
import com.nhaarman.acorn.android.util.inflate
import com.nhaarman.acorn.notesapp.android.R
import com.nhaarman.acorn.notesapp.android.ui.itemlist.ItemListViewController
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