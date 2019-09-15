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

package com.nhaarman.acorn.samples.hellobottombar.news

import android.view.ViewGroup
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.samples.hellobottombar.MainLayout
import com.nhaarman.acorn.samples.hellobottombar.R

class NewsViewControllerFactory : ViewControllerFactory {

    override fun supports(scene: Scene<*>): Boolean {
        return scene is NewsScene
    }

    override fun viewControllerFor(scene: Scene<*>, parent: ViewGroup): ViewController {
        return NewsViewController(
            MainLayout.inflateWith(R.layout.news_scene, parent)
        )
    }
}
