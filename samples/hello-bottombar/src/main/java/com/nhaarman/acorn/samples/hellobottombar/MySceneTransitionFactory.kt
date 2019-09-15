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

package com.nhaarman.acorn.samples.hellobottombar

import android.view.ViewGroup
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.nhaarman.acorn.android.transition.SceneTransition
import com.nhaarman.acorn.android.transition.SceneTransitionFactory
import com.nhaarman.acorn.android.util.inflateView
import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.samples.hellobottombar.favorites.FavoritesScene
import com.nhaarman.acorn.samples.hellobottombar.favorites.FavoritesViewController
import com.nhaarman.acorn.samples.hellobottombar.music.MusicScene
import com.nhaarman.acorn.samples.hellobottombar.music.MusicViewController
import com.nhaarman.acorn.samples.hellobottombar.news.NewsScene
import com.nhaarman.acorn.samples.hellobottombar.news.NewsViewController
import com.nhaarman.acorn.samples.hellobottombar.places.PlacesScene
import com.nhaarman.acorn.samples.hellobottombar.places.PlacesViewController

class MySceneTransitionFactory : SceneTransitionFactory {

    override fun supports(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): Boolean {
        return true
    }

    override fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): SceneTransition {
        return object : SceneTransition {
            override fun execute(parent: ViewGroup, callback: SceneTransition.Callback) {
                val contentContainer = parent.findViewById<ViewGroup>(R.id.contentContainer)

                TransitionManager.beginDelayedTransition(contentContainer, AutoTransition().also { it.duration = 100 })

                contentContainer.removeAllViews()
                contentContainer.inflateView(
                    when (newScene.key) {
                        PlacesScene.key -> R.layout.places_scene
                        MusicScene.key -> R.layout.music_scene
                        FavoritesScene.key -> R.layout.favorites_scene
                        NewsScene.key -> R.layout.news_scene
                        else -> error("Unknown scene: $newScene")
                    },
                    true
                )

                callback.onComplete(
                    when (newScene.key) {
                        PlacesScene.key -> PlacesViewController(parent)
                        MusicScene.key -> MusicViewController(parent)
                        FavoritesScene.key -> FavoritesViewController(parent)
                        NewsScene.key -> NewsViewController(parent)
                        else -> error("Unknown scene: $newScene")
                    }
                )
            }
        }
    }
}
