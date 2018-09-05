package com.nhaarman.bravo.android.presentation

import android.view.ViewGroup
import com.nhaarman.bravo.presentation.SceneKey

/**
 * A [ViewFactory] implementation that binds [SceneKey]s to [ViewCreator]
 * instances to create views.
 */
internal class BindingViewFactory(
    private val bindings: Map<SceneKey, ViewCreator>
) : ViewFactory {

    override fun viewFor(sceneKey: SceneKey, parent: ViewGroup): ViewResult? {
        return bindings[sceneKey]?.create(parent)
    }
}
