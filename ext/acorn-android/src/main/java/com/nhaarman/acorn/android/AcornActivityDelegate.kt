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
import com.nhaarman.acorn.OnBackPressListener
import com.nhaarman.acorn.android.dispatching.AcornSceneDispatcher
import com.nhaarman.acorn.android.dispatching.SceneDispatcher
import com.nhaarman.acorn.android.dispatching.SceneDispatcherFactory
import com.nhaarman.acorn.android.experimental.AcornEvents
import com.nhaarman.acorn.android.experimental.ExperimentalAcornEvents
import com.nhaarman.acorn.android.experimental.HookingSceneDispatcher
import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.android.presentation.ActivityController
import com.nhaarman.acorn.android.presentation.ActivityControllerFactory
import com.nhaarman.acorn.android.presentation.NoopActivityControllerFactory
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.transition.DefaultSceneTransitionFactory
import com.nhaarman.acorn.android.transition.SceneTransition
import com.nhaarman.acorn.android.transition.SceneTransitionFactory
import com.nhaarman.acorn.android.util.toBundle
import com.nhaarman.acorn.android.util.toNavigatorState
import com.nhaarman.acorn.navigation.DisposableHandle
import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.SavedState

/**
 * A convenience class which deals with the Activity lifecycle and connecting
 * a [SceneDispatcher] to the [Navigator].
 */
class AcornActivityDelegate private constructor(
    private val navigatorProvider: NavigatorProvider,
    private val sceneDispatcherFactory: SceneDispatcherFactory
) {

    private lateinit var navigator: Navigator

    /**
     * Returns the navigator used in this instance.
     * Must only be called _after_ [onCreate] has been called.
     */
    fun navigator(): Navigator {
        return navigator
    }

    private lateinit var dispatcher: SceneDispatcher

    private var disposable: DisposableHandle? = null
        set(value) {
            field?.dispose()
            field = value
        }

    fun onCreate(savedInstanceState: Bundle?) {
        dispatcher = sceneDispatcherFactory.invoke(savedInstanceState.sceneDispatcherState)
        navigator = navigatorProvider.navigatorFor(savedInstanceState.navigatorState)
        disposable = dispatcher.dispatchScenesFor(navigator)
    }

    fun onStart() {
        navigator.onStart()
        dispatcher.onUIVisible()
    }

    // We suppress the unused parameter to keep a uniform API.
    @Suppress("UNUSED_PARAMETER")
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        dispatcher.onActivityResult(resultCode, data)
    }

    fun onBackPressed(): Boolean {
        if (dispatcher.onBackPressed()) {
            return true
        }

        return (navigator as? OnBackPressListener)?.onBackPressed() ?: false
    }

    fun onStop() {
        dispatcher.onUINotVisible()
    }

    fun onDestroy() {
        disposable = null
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.navigatorState = navigatorProvider.saveNavigatorState()
        outState.sceneDispatcherState = dispatcher.saveInstanceState()
    }

    companion object {

        /**
         * Creates a new [AcornActivityDelegate].
         *
         * @param sceneDispatcherFactory The [SceneDispatcherFactory] instance
         * that will provide a [SceneDispatcher] instance. When providing a
         * custom [SceneDispatcherFactory] one must be careful to invoke
         * [AcornEvents.onStartDispatching] and [AcornEvents.onStopDispatching]
         * at the right times, or make use of the [HookingSceneDispatcher].
         * @param navigatorProvider The [NavigatorProvider] instance that will
         * provide a [Navigator] instance.
         */
        fun from(
            sceneDispatcherFactory: SceneDispatcherFactory,
            navigatorProvider: NavigatorProvider
        ): AcornActivityDelegate {
            return AcornActivityDelegate(
                navigatorProvider,
                sceneDispatcherFactory
            )
        }

        /**
         * Creates a new [AcornActivityDelegate] which uses a default
         * [SceneDispatcher] and the default root [ViewGroup].
         *
         * @param activity The [Activity] which delegates to the resulting
         * instance.
         * @param navigatorProvider The [NavigatorProvider] instance that will
         * provide a [Navigator] instance.
         * @param viewControllerFactory The [ViewControllerFactory] instance
         * that is used to create [ViewController] instances when dispatching
         * Scenes.
         * @param activityControllerFactory The [ActivityControllerFactory]
         * instance that is used to create [ActivityController] instances when
         * dispatching Scenes. Defaults to [NoopActivityControllerFactory].
         * @param transitionFactory the [SceneTransitionFactory] instance that is
         * used to create [SceneTransition] instances when animating Scene
         * transitions. Defaults to [DefaultSceneTransitionFactory].
         */
        @Deprecated(
            "" +
                "Use from(Activity, ViewGroup, NavigatorProvider, ViewControllerFactory, ActivityControllerFactory, SceneTransitionFactory) " +
                "instead and provide the root ViewGroup to use.\n" +
                "This method uses the android.R.id.content ViewGroup, which can result in theming issues.",
            level = DeprecationLevel.WARNING
        )
        fun from(
            activity: Activity,
            navigatorProvider: NavigatorProvider,
            viewControllerFactory: ViewControllerFactory,
            activityControllerFactory: ActivityControllerFactory = NoopActivityControllerFactory,
            transitionFactory: SceneTransitionFactory = DefaultSceneTransitionFactory(viewControllerFactory)
        ): AcornActivityDelegate {
            return AcornActivityDelegate.from(
                activity,
                activity.findViewById(android.R.id.content),
                navigatorProvider,
                viewControllerFactory,
                activityControllerFactory,
                transitionFactory
            )
        }

        /**
         * Creates a new [AcornActivityDelegate] which uses a default
         * [SceneDispatcher] and the default root [ViewGroup].
         *
         * @param activity The [Activity] which delegates to the resulting
         * instance.
         * @param root The root [ViewGroup] to use as the parent for all Scene
         * layouts.
         * @param navigatorProvider The [NavigatorProvider] instance that will
         * provide a [Navigator] instance.
         * @param viewControllerFactory The [ViewControllerFactory] instance
         * that is used to create [ViewController] instances when dispatching
         * Scenes.
         * @param activityControllerFactory The [ActivityControllerFactory]
         * instance that is used to create [ActivityController] instances when
         * dispatching Scenes. Defaults to [NoopActivityControllerFactory].
         * @param transitionFactory the [SceneTransitionFactory] instance that is
         * used to create [SceneTransition] instances when animating Scene
         * transitions. Defaults to [DefaultSceneTransitionFactory].
         */
        @UseExperimental(ExperimentalAcornEvents::class)
        fun from(
            activity: Activity,
            root: ViewGroup,
            navigatorProvider: NavigatorProvider,
            viewControllerFactory: ViewControllerFactory,
            activityControllerFactory: ActivityControllerFactory = NoopActivityControllerFactory,
            transitionFactory: SceneTransitionFactory = DefaultSceneTransitionFactory(viewControllerFactory)
        ): AcornActivityDelegate {
            return AcornActivityDelegate.from(
                HookingSceneDispatcherFactory(
                    activity,
                    root,
                    navigatorProvider,
                    viewControllerFactory,
                    activityControllerFactory,
                    transitionFactory
                ),
                navigatorProvider
            )
        }

        private var Bundle?.navigatorState: NavigatorState?
            get() = this?.getBundle("navigator")?.toNavigatorState()
            set(value) {
                this?.putBundle("navigator", value?.toBundle())
            }

        private var Bundle?.sceneDispatcherState: SavedState?
            get() = this?.getBundle("scene_dispatcher")?.toNavigatorState()
            set(value) {
                this?.putBundle("scene_dispatcher", value?.toBundle())
            }
    }

    @ExperimentalAcornEvents
    private class HookingSceneDispatcherFactory(
        private val activity: Activity,
        private val root: ViewGroup,
        private val navigatorProvider: NavigatorProvider,
        private val viewControllerFactory: ViewControllerFactory,
        private val activityControllerFactory: ActivityControllerFactory,
        private val transitionFactory: SceneTransitionFactory
    ) : SceneDispatcherFactory {

        override fun invoke(savedState: SavedState?): SceneDispatcher {
            return HookingSceneDispatcher.create(
                AcornSceneDispatcher.create(
                    activity,
                    root,
                    viewControllerFactory,
                    activityControllerFactory,
                    transitionFactory,
                    DefaultAcornSceneDispatcherCallback(activity),
                    savedState
                ),
                navigatorProvider
            )
        }
    }

    private class DefaultAcornSceneDispatcherCallback(private val activity: Activity) : AcornSceneDispatcher.Callback {

        override fun startForResult(intent: Intent) {
            activity.startActivityForResult(intent, 42)
        }

        override fun finished() {
            activity.finish()
        }
    }
}
