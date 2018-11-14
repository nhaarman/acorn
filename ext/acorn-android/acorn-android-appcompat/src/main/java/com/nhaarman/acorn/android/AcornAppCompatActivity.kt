/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.acorn.android

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.android.presentation.ActivityControllerFactory
import com.nhaarman.acorn.android.presentation.ActivityController
import com.nhaarman.acorn.android.presentation.ComposingViewControllerFactory
import com.nhaarman.acorn.android.presentation.NoopActivityControllerFactory
import com.nhaarman.acorn.android.presentation.NoopViewControllerFactory
import com.nhaarman.acorn.android.presentation.SceneViewControllerFactory
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.transition.DefaultTransitionFactory
import com.nhaarman.acorn.android.transition.Transition
import com.nhaarman.acorn.android.transition.TransitionFactory
import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.presentation.Scene

/**
 * A base [AppCompatActivity] implementation to simplify Acorn usage.
 *
 * If you can't or don't want to inherit from this class, you can use the
 * [AcornActivityDelegate] class and route the necessary Activity function
 * calls to it.
 */
abstract class AcornAppCompatActivity : AppCompatActivity() {

    /**
     * Returns the [NavigatorProvider] to use in this Activity.
     *
     * [NavigatorProvider] instances should be shared across instances, so
     * make sure you cache this instance outside of this Activity.
     */
    protected abstract fun provideNavigatorProvider(): NavigatorProvider

    /**
     * Returns the [ViewControllerFactory] that can provide
     * [ViewController] instances for this Activity.
     *
     * The instance returned here will be combined with a
     * [SceneViewControllerFactory] to be able to use [Scene] instances as
     * ViewController factories.
     *
     * Returns [NoopViewControllerFactory] by default.
     */
    protected open fun provideViewControllerFactory(): ViewControllerFactory {
        return NoopViewControllerFactory
    }

    /**
     * Returns the [TransitionFactory] to create [Transition] instances
     * for this Activity.
     *
     * By default, this returns a [DefaultTransitionFactory].
     *
     * @param viewControllerFactory The [ViewControllerFactory] as returned by
     * [provideViewControllerFactory].
     */
    protected open fun provideTransitionFactory(viewControllerFactory: ViewControllerFactory): TransitionFactory {
        return DefaultTransitionFactory(viewControllerFactory)
    }

    /**
     * Returns the [ActivityControllerFactory] that can provide
     * [ActivityController] instances when using external Activities.
     */
    protected open fun provideActivityControllerFactory(): ActivityControllerFactory {
        return NoopActivityControllerFactory
    }

    private val navigatorProvider: NavigatorProvider by lazy {
        provideNavigatorProvider()
    }

    private val viewControllerFactory: ViewControllerFactory by lazy {
        ComposingViewControllerFactory.from(
            SceneViewControllerFactory,
            provideViewControllerFactory()
        )
    }

    private val transitionFactory: TransitionFactory by lazy {
        provideTransitionFactory(viewControllerFactory)
    }

    private val activityControllerFactory: ActivityControllerFactory by lazy {
        provideActivityControllerFactory()
    }

    /**
     * Returns the navigator used in this instance.
     * Must only be called _after_ [onCreate] has been called.
     */
    protected fun navigator(): Navigator {
        return acornDelegate.navigator()
    }

    private val acornDelegate: AcornActivityDelegate by lazy {
        AcornActivityDelegate.from(
            activity = this,
            navigatorProvider = navigatorProvider,
            viewControllerFactory = viewControllerFactory,
            activityControllerFactory = activityControllerFactory,
            transitionFactory = transitionFactory
        )
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        acornDelegate.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        acornDelegate.onStart()
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        acornDelegate.onActivityResult(requestCode, resultCode, data)
    }

    @CallSuper
    override fun onBackPressed() {
        if (!acornDelegate.onBackPressed()) {
            super.onBackPressed()
        }
    }

    @CallSuper
    override fun onStop() {
        acornDelegate.onStop()
        super.onStop()
    }

    @CallSuper
    override fun onDestroy() {
        acornDelegate.onDestroy()
        super.onDestroy()
    }

    /**
     * [AppCompatActivity.onSaveInstanceState] saves the view hierarchy state,
     * which is something we do manually. Therefore we do not call the super
     * implementation.
     */
    @SuppressLint("MissingSuperCall")
    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        acornDelegate.onSaveInstanceState(outState)
    }
}