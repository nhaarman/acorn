package com.nhaarman.bravo.samples.hellostaterestoration

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.nhaarman.bravo.android.presentation.RestorableView
import com.nhaarman.bravo.presentation.RestorableContainer
import kotlinx.android.synthetic.main.myscene.view.*

/**
 * An interface describing the view.
 *
 * Implements [RestorableContainer] to be able to save and restore its state.
 */
interface HelloStateRestorationContainer : RestorableContainer {

    /**
     * A counter value to be shown.
     */
    var counterValue: Int

    /**
     * Register
     */
    fun onNextClicked(f: () -> Unit)
}

/**
 * A [View] implementation implementing the [HelloStateRestorationContainer].
 *
 * Implements [RestorableView] to use a default implementation of saving and
 * restoring view state.
 */
class HelloStateRestorationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    HelloStateRestorationContainer,
    RestorableView {

    override var counterValue: Int = 0
        set(value) {
            counterTV.text = "$value"
        }

    override fun onNextClicked(f: () -> Unit) {
        nextButton.setOnClickListener { f.invoke() }
    }
}
