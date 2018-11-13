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
import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import com.nhaarman.bravo.android.navigation.NavigatorProvider
import com.nhaarman.bravo.android.presentation.IntentProvider
import com.nhaarman.bravo.android.presentation.NoIntentProvider
import com.nhaarman.bravo.android.presentation.ViewControllerFactory
import com.nhaarman.bravo.android.transition.DefaultTransitionFactory
import com.nhaarman.bravo.android.transition.Transition
import com.nhaarman.bravo.android.transition.TransitionFactory
import com.nhaarman.bravo.navigation.Navigator

/**
 * A base [Activity] implementation to simplify Bravo usage.
 *
 * If you can't or don't want to inherit from this class, you can use the
 * [BravoActivityDelegate] class and route the necessary Activity function
 * calls to it.
 */
abstract class BravoActivity : Activity() {

    /**
     * Returns the [NavigatorProvider] to use in this Activity.
     *
     * [NavigatorProvider] instances should be shared across instances, so
     * make sure you cache this instance outside of this Activity.
     */
    protected abstract fun provideNavigatorProvider(): NavigatorProvider

    /**
     * Returns the [ViewControllerFactory] that can provide views for this Activity.
     */
    protected abstract fun provideViewFactory(): ViewControllerFactory

    /**
     * Returns the [TransitionFactory] to create [Transition] instances
     * for this Activity.
     *
     * By default, this returns a [DefaultTransitionFactory].
     */
    protected open fun provideTransitionFactory(): TransitionFactory {
        return DefaultTransitionFactory(viewControllerFactory)
    }

    /**
     * Returns the [IntentProvider] instance that can create [Intent] instances
     * for Scenes.
     *
     * By default, this function returns an IntentProvider that always returns
     * `null`.
     */
    protected open fun provideIntentProvider(): IntentProvider {
        return NoIntentProvider
    }

    private val navigatorProvider: NavigatorProvider by lazy {
        provideNavigatorProvider()
    }

    private val viewControllerFactory: ViewControllerFactory by lazy {
        provideViewFactory()
    }

    private val transitionFactory: TransitionFactory by lazy {
        provideTransitionFactory()
    }

    private val intentProvider: IntentProvider by lazy {
        provideIntentProvider()
    }

    /**
     * Returns the navigator used in this instance.
     * Must only be called _after_ [onCreate] has been called.
     */
    protected fun navigator(): Navigator {
        return bravoDelegate.navigator()
    }

    private val bravoDelegate by lazy {
        BravoActivityDelegate.from(
            activity = this,
            navigatorProvider = navigatorProvider,
            viewControllerFactory = viewControllerFactory,
            transitionFactory = transitionFactory,
            intentProvider = intentProvider
        )
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bravoDelegate.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        bravoDelegate.onStart()
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        bravoDelegate.onActivityResult(requestCode, resultCode, data)
    }

    @CallSuper
    override fun onBackPressed() {
        if (!bravoDelegate.onBackPressed()) {
            super.onBackPressed()
        }
    }

    @CallSuper
    override fun onStop() {
        bravoDelegate.onStop()
        super.onStop()
    }

    @CallSuper
    override fun onDestroy() {
        bravoDelegate.onDestroy()
        super.onDestroy()
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        bravoDelegate.onSaveInstanceState(outState)
    }
}