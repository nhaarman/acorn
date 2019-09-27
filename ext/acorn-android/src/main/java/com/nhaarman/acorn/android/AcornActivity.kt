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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.CallSuper
import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.android.presentation.ActivityController
import com.nhaarman.acorn.android.presentation.ActivityControllerFactory
import com.nhaarman.acorn.android.presentation.ComposingViewControllerFactory
import com.nhaarman.acorn.android.presentation.NoopActivityControllerFactory
import com.nhaarman.acorn.android.presentation.NoopViewControllerFactory
import com.nhaarman.acorn.android.presentation.SceneViewControllerFactory
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.transition.ComposingSceneTransitionFactory
import com.nhaarman.acorn.android.transition.DefaultSceneTransitionFactory
import com.nhaarman.acorn.android.transition.NoopSceneTransitionFactory
import com.nhaarman.acorn.android.transition.SceneTransition
import com.nhaarman.acorn.android.transition.SceneTransitionFactory
import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.presentation.Scene

/**
 * A base [Activity] implementation to simplify Acorn usage.
 *
 * If you can't or don't want to inherit from this class, you can use the
 * [AcornActivityDelegate] class and route the necessary Activity function
 * calls to it.
 */
abstract class AcornActivity : Activity() {

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
     * Returns the [SceneTransitionFactory] to create [SceneTransition] instances
     * for this Activity. The resulting factory instance will be combined with
     * a [DefaultSceneTransitionFactory] as fallback.
     *
     * By default, this returns a [NoopSceneTransitionFactory].
     *
     * @param viewControllerFactory The [ViewControllerFactory] as returned by
     * [provideViewControllerFactory].
     */
    protected open fun provideTransitionFactory(viewControllerFactory: ViewControllerFactory): SceneTransitionFactory {
        return NoopSceneTransitionFactory
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
     *
     * This method will be called once by Acorn, so it is safe to create new instances here.
     *
     * Override this method if you want to provide your own ViewGroup implementation.
     * If the returned ViewGroup has no parent, it will be passed to a call to [setContentView].
     *
     * This method returns `null` by default, which will result in an empty [ViewGroup] being
     * used as the content view.
     *
     * @return a ViewGroup to be used as the root view, or `null` to fall back to default behavior.
     * @see rootView
     */
    protected open fun provideRootView(): ViewGroup? {
        return null
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

    private val transitionFactory: SceneTransitionFactory by lazy {
        ComposingSceneTransitionFactory.from(
            provideTransitionFactory(viewControllerFactory),
            DefaultSceneTransitionFactory(viewControllerFactory)
        )
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

    private val rootView by lazy {
        val rootView = provideRootView() ?: findViewById(android.R.id.content)
        if (rootView.parent == null) {
            setContentView(rootView)
        }
        rootView
    }

    private val acornDelegate: AcornActivityDelegate by lazy {
        AcornActivityDelegate.from(
            activity = this,
            root = rootView,
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

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        acornDelegate.onSaveInstanceState(outState)
    }
}
