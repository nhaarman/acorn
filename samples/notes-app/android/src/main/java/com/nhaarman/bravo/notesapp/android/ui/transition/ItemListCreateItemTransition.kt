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

import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.core.animation.addListener
import com.nhaarman.bravo.android.presentation.ViewResult
import com.nhaarman.bravo.android.transition.Transition
import com.nhaarman.bravo.android.util.inflateView
import com.nhaarman.bravo.notesapp.android.R
import com.nhaarman.bravo.notesapp.android.ui.createitem.CreateItemView
import kotlinx.android.synthetic.main.itemlist_scene.view.*

/**
 * A [Transition] that shows a circular reveal animation to transition from
 * the ItemList layout to the CreateItem layout.
 */
object ItemListCreateItemTransition : Transition {

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        val itemListLayout = parent.getChildAt(0)
        val view = parent.inflateView(R.layout.itemlistcreateitem_reveal)
        parent.addView(view)

        val anim = ViewAnimationUtils.createCircularReveal(
            view,
            (parent.createButton.x.toInt() + parent.createButton.width / 2),
            parent.createButton.y.toInt() + parent.createButton.height / 2,
            0f,
            Math.hypot(parent.width.toDouble(), parent.height.toDouble()).toFloat()
        )

        anim.duration = parent.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        anim.addListener(onEnd = {
            val viewResult = ViewResult.from(parent, CreateItemView(parent))

            parent.removeView(itemListLayout)
            val createItemLayout = parent.inflateView(R.layout.createitem_scene)
            parent.addView(createItemLayout, 0)
            callback.attach(viewResult)

            view.animate()
                .alpha(0f)
                .setDuration(parent.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .withEndAction {
                    parent.removeView(view)
                    callback.onComplete(viewResult)
                }
        })
        anim.start()
    }
}