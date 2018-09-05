package com.nhaarman.bravo.android.presentation

import android.view.ViewGroup
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.presentation.SceneKey

/**
 * A factory interface that can create view instances for [Scene]s.
 */
interface ViewFactory {

    /**
     * Creates a [ViewResult] for given Scene key.
     *
     * @param sceneKey The key of the [Scene] instance for which the
     * corresponding view should be created.
     * @param parent If non-null, this is the parent view that the resulting
     * view should be attached to. The implementation must not add the view
     * to the parent itself, but this can be used to generate the LayoutParams
     * of the view.
     *
     * @return The resulting [ViewResult]. `null` if no result could be created
     * for given [sceneKey].
     */
    fun viewFor(sceneKey: SceneKey, parent: ViewGroup): ViewResult?
}