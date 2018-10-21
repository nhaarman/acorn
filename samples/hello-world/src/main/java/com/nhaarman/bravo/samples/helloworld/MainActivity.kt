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

package com.nhaarman.bravo.samples.helloworld

import com.nhaarman.bravo.android.BravoActivity
import com.nhaarman.bravo.android.navigation.NavigatorProvider
import com.nhaarman.bravo.android.presentation.ViewFactory
import com.nhaarman.bravo.android.presentation.bindViews
import com.nhaarman.bravo.presentation.SceneKey.Companion.defaultKey

/**
 * The Activity that hosts this sample.
 *
 * Interesting components:
 *
 *  - [HelloWorldNavigator] manages the navigation flow, in this case showing
 *    a simple Scene.
 *  - [HelloWorldScene] presents a text to the UI when available.
 *  - [HelloWorldView] implements the view to show the text.
 */
class MainActivity : BravoActivity() {

    override fun provideNavigatorProvider(): NavigatorProvider {
        return HelloWorldNavigatorProvider
    }

    override fun provideViewFactory(): ViewFactory {
        return bindViews {
            bind(
                defaultKey<HelloWorldScene>(),
                R.layout.hello_world,
                ::HelloWorldViewController
            )
        }
    }
}