package com.nhaarman.bravo.android.presentation

import android.app.Activity
import android.view.View
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

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
         * [Activity] layout. This [View] instance must implement
         * [Container], or an [IllegalArgumentException] is thrown.
         */
        fun from(view: View): ViewResult {
            if (view !is Container) {
                throw IllegalArgumentException("View should implement ${Container::class.java.name}: $view")
            }

            return ViewResult(view = view, container = view)
        }

        /**
         * Creates a new [ViewResult] from given [View] and [Container].
         *
         * @param view The root view for its Scene, to be attached to the
         * [Activity] layout.
         * @param container A [Container] instance that can be passed to the
         * [Scene] and which delegates the [Scene]'s commands
         * to [view].
         */
        fun from(view: View, container: Container): ViewResult {
            return ViewResult(view, container)
        }
    }
}