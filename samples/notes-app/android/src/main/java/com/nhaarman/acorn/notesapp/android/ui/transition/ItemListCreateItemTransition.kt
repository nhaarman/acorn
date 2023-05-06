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

import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.core.animation.addListener
import com.nhaarman.acorn.android.transition.SceneTransition
import com.nhaarman.acorn.android.util.inflateView
import com.nhaarman.acorn.notesapp.android.R
import com.nhaarman.acorn.notesapp.android.ui.createitem.CreateItemViewController

/**
 * A [SceneTransition] that shows a circular reveal animation to transition from
 * the ItemList layout to the CreateItem layout.
 */
object ItemListCreateItemTransition : SceneTransition {

    override fun execute(parent: ViewGroup, callback: SceneTransition.Callback) {
        val itemListLayout = parent.getChildAt(0)
        val view = parent.inflateView(R.layout.itemlistcreateitem_reveal)
        parent.addView(view)

        val createButton = parent.findViewById<View>(R.id.createButton)
        val anim = ViewAnimationUtils.createCircularReveal(
            view,
            (createButton.x.toInt() + createButton.width / 2),
            createButton.y.toInt() + createButton.height / 2,
            0f,
            Math.hypot(parent.width.toDouble(), parent.height.toDouble()).toFloat(),
        )

        anim.duration = parent.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        anim.addListener(
            onEnd = {
                val viewController = CreateItemViewController(parent)

                parent.removeView(itemListLayout)
                val createItemLayout = parent.inflateView(R.layout.createitem_scene)
                parent.addView(createItemLayout, 0)
                callback.attach(viewController)

                view.animate()
                    .alpha(0f)
                    .setDuration(parent.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                    .withEndAction {
                        parent.removeView(view)
                        callback.onComplete(viewController)
                    }
            },
        )
        anim.start()
    }
}
