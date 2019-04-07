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

package com.nhaarman.acorn.android

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.android.presentation.ActivityController
import com.nhaarman.acorn.android.presentation.ActivityControllerFactory
import com.nhaarman.acorn.android.presentation.ComposingViewControllerFactory
import com.nhaarman.acorn.android.presentation.NoopActivityControllerFactory
import com.nhaarman.acorn.android.presentation.NoopViewControllerFactory
import com.nhaarman.acorn.android.presentation.SceneViewControllerFactory
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.transition.DefaultTransitionFactory
import com.nhaarman.acorn.android.transition.SceneTransition
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
     * Returns the [TransitionFactory] to create [SceneTransition] instances
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

    /**
     * Returns the root [ViewGroup] that is used to inflate Scene views in.
     */
    protected open fun provideRootView(): ViewGroup {
        return findViewById(android.R.id.content)
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
            root = provideRootView(),
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
