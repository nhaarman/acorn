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

package com.nhaarman.bravo.android

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import com.nhaarman.bravo.OnBackPressListener
import com.nhaarman.bravo.android.navigation.NavigatorProvider
import com.nhaarman.bravo.android.presentation.ActivityState
import com.nhaarman.bravo.android.presentation.ViewFactory
import com.nhaarman.bravo.android.transition.TransitionFactory
import com.nhaarman.bravo.android.util.toBundle
import com.nhaarman.bravo.android.util.toNavigatorState
import com.nhaarman.bravo.navigation.DisposableHandle
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.navigation.TransitionData
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.util.lazyVar

class BravoActivityDelegate(
    private val activity: Activity,
    private val navigatorProvider: NavigatorProvider,
    private val viewFactory: ViewFactory,
    private val transitionFactory: TransitionFactory
) {

    private lateinit var navigator: Navigator<Navigator.Events>

    private var state by lazyVar {
        ActivityState.create(activity.root, viewFactory, transitionFactory)
    }

    private var disposable: DisposableHandle? = null
        set(value) {
            field?.dispose()
            field = value
        }

    @Suppress("UNCHECKED_CAST")
    fun onCreate(savedInstanceState: Bundle?) {
        navigator = navigatorProvider.navigatorFor(savedInstanceState.navigatorState) as Navigator<Navigator.Events>

        // TODO: What should the main Navigator lifecycle be like?
        navigator.onStart()

        disposable = navigator.addListener(MyListener())
    }

    fun onStart() {
        state = state.started()
    }

    fun onBackPressed(): Boolean {
        return (navigator as? OnBackPressListener)?.onBackPressed() ?: false
    }

    fun onStop() {
        state = state.stopped()
    }

    fun onDestroy() {
        disposable = null
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.navigatorState = navigatorProvider.saveNavigatorState()
    }

    private inner class MyListener : Navigator.Events {

        override fun scene(scene: Scene<out Container>, data: TransitionData?) {
            state = state.withScene(scene, data)
        }

        override fun finished() {
            activity.finish()
        }
    }

    companion object {

        private val Activity.root get() = findViewById<ViewGroup>(android.R.id.content)

        private var Bundle?.navigatorState: NavigatorState?
            get() = this?.getBundle("navigator")?.toNavigatorState()
            set(value) {
                this?.putBundle("navigator", value?.toBundle())
            }
    }
}