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

package com.nhaarman.acorn.samples.helloviewfactory

import com.nhaarman.acorn.android.AcornActivity
import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.presentation.bindViews
import com.nhaarman.acorn.presentation.SceneKey

class MainActivity : AcornActivity() {

    override fun provideNavigatorProvider(): NavigatorProvider {
        return HelloViewFactoryNavigatorProvider
    }

    override fun provideViewControllerFactory(): ViewControllerFactory {
        return bindViews {
            bind(
                SceneKey.defaultKey<HelloViewFactoryScene>(),
                R.layout.myscene,
                ::HelloViewFactoryViewController
            )
        }
    }
}
