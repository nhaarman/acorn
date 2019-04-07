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

package com.nhaarman.acorn.android.util

import android.view.ViewGroup
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.transition.SceneTransition

class TestTransition : SceneTransition {

    private var callback: SceneTransition.Callback? = null
        set(value) {
            if (field != null) error("SceneTransition already executed")
            field = value
        }

    override fun execute(parent: ViewGroup, callback: SceneTransition.Callback) {
        this.callback = callback
    }

    fun isStarted() = callback != null

    fun attach(viewController: ViewController) {
        callback!!.attach(viewController)
    }

    fun complete(viewController: ViewController) {
        callback!!.onComplete(viewController)
    }
}
