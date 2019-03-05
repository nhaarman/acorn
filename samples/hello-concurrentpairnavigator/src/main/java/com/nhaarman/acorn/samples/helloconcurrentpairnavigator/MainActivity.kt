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

package com.nhaarman.acorn.samples.helloconcurrentpairnavigator

import android.view.ViewGroup
import com.nhaarman.acorn.android.AcornAppCompatActivity
import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.transition.TransitionFactory
import com.nhaarman.acorn.android.transition.transitionFactory
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey

class MainActivity : AcornAppCompatActivity() {

    override fun provideNavigatorProvider(): NavigatorProvider {
        return HelloNavigationNavigatorProvider
    }

    override fun provideViewControllerFactory(): ViewControllerFactory {
        return object : ViewControllerFactory {

            override fun supports(scene: Scene<*>): Boolean {
                return scene.key == SceneKey.defaultKey<SecondScene>()
            }

            override fun viewControllerFor(scene: Scene<*>, parent: ViewGroup): ViewController {
                return FirstSecondViewController(parent)
            }
        }
    }

    override fun provideTransitionFactory(viewControllerFactory: ViewControllerFactory): TransitionFactory {
        return transitionFactory(viewControllerFactory) {
            (SceneKey.defaultKey<FirstScene>() to SceneKey.defaultKey<SecondScene>()) use FirstSecondTransition
            (SceneKey.defaultKey<SecondScene>() to SceneKey.defaultKey<FirstScene>()) use SecondFirstTransition
        }
    }
}
