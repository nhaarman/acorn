package com.nhaarman.bravo.android.transition

import android.view.View
import android.view.ViewGroup
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

/**
 * A factory interface that can create view instances for [Scene]s.
 */
interface ViewFactory {

    /**
     * Creates a [ViewResult] for given Scene key..
     *
     * @param sceneKey The key of the [Scene] instance for which the
     *                 corresponding view should be created.
     * @param parent If non-null, this is the parent view that the resulting
     *               view should be attached to. The implementation must not add the view
     *               to the parent itself, but this can be used to generate the LayoutParams
     *               of the view.
     *
     * @return The resulting [ViewResult].
     */
    fun viewFor(sceneKey: String, parent: ViewGroup): ViewResult
}

/**
 * A wrapper class for a Scene's view and container.
 */
class ViewResult private constructor(
    val view: View,
    val container: Container
) {

    companion object {

        /**
         * Creates a new [ViewResult] from given [View].
         *
         * @param view The root view for its [Scene], to be attached to the
         *             [Activity] layout. This [View] instance must implement
         *             [Container], or an [IllegalArgumentException] is thrown.
         */
        fun from(view: View): ViewResult {
            if (view !is Container) throw IllegalStateException("View should implement ${Container::class.java.name}: $view")

            return ViewResult(view = view, container = view)
        }

        /**
         * Creates a new [ViewResult] from given [View] and [Container].
         *
         * @param view The root view for its Scene, to be attached to the
         *             [Activity] layout.
         * @param container A [Container] instance that can be passed to the
         *                  [Scene] and which delegates the [Scene]'s commands
         *                  to [view].
         */
        fun from(view: View, container: Container): ViewResult {
            return ViewResult(view, container)
        }
    }
}
