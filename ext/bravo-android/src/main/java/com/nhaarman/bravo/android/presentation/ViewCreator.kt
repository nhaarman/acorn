package com.nhaarman.bravo.android.presentation

import android.support.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup
import com.nhaarman.bravo.android.util.inflate
import com.nhaarman.bravo.android.util.inflateView
import com.nhaarman.bravo.presentation.Container

/**
 * Designates a class that can create a [View] and [Container] instance when
 * needed.
 */
internal interface ViewCreator {

    /**
     * Creates the [View] and [Container] instances.
     *
     * @param parent The parent [ViewGroup] the result will be added to.
     * Implementers must not add the result to the parent manually.
     */
    fun create(parent: ViewGroup): ViewResult
}

internal class LayoutResourceViewCreator(
    @LayoutRes private val layoutResId: Int
) : ViewCreator {

    override fun create(parent: ViewGroup): ViewResult {
        return parent
            .inflateView(layoutResId)
            .let { ViewResult.from(it) }
    }
}

internal class WrappedLayoutResourceViewCreator<V : View>(
    @LayoutRes private val layoutResId: Int,
    private val wrapper: (V) -> Container
) : ViewCreator {

    override fun create(parent: ViewGroup): ViewResult {
        return parent
            .inflate<V>(layoutResId)
            .let { view -> ViewResult.from(view, wrapper.invoke(view)) }
    }
}