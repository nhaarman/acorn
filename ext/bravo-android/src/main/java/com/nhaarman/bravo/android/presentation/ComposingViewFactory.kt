package com.nhaarman.bravo.android.presentation

import android.view.ViewGroup
import com.nhaarman.bravo.presentation.SceneKey

/**
 * A [ViewFactory] implementation that can delegate to other implementations.
 *
 * When a view is requested, the source factories are queried in-order until
 * a valid result is found.
 */
class ComposingViewFactory(
    private val sources: List<ViewFactory>
) : ViewFactory {

    override fun viewFor(sceneKey: SceneKey, parent: ViewGroup): ViewResult? {
        return sources
            .asSequence()
            .mapNotNull { it.viewFor(sceneKey, parent) }
            .firstOrNull()
    }
}