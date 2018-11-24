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

package com.nhaarman.bravo.samples.hellostartactivity

import android.content.Context
import com.nhaarman.bravo.android.BravoAppCompatActivity
import com.nhaarman.bravo.android.navigation.NavigatorProvider
import com.nhaarman.bravo.android.presentation.ActivityController
import com.nhaarman.bravo.android.presentation.ActivityControllerFactory
import com.nhaarman.bravo.android.presentation.ViewControllerFactory
import com.nhaarman.bravo.android.presentation.bindViews
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.presentation.SceneKey
import com.nhaarman.bravo.presentation.SceneKey.Companion.defaultKey

class MainActivity : BravoAppCompatActivity() {

    override fun provideNavigatorProvider(): NavigatorProvider {
        return HelloStartActivityNavigatorProvider
    }

    override fun provideViewControllerFactory(): ViewControllerFactory {
        return bindViews {
            bindView(defaultKey<FirstScene>(), R.layout.first_scene) {
                FirstSceneViewController(it)
            }
        }
    }

    override fun provideActivityControllerFactory(): ActivityControllerFactory {
        return object : ActivityControllerFactory {

            override fun supports(sceneKey: SceneKey): Boolean {
                return sceneKey == SceneKey.from(MapsScene::class)
            }

            override fun activityControllerFor(scene: Scene<*>, context: Context): ActivityController {
                return MapsActivityController()
            }
        }
    }
}
