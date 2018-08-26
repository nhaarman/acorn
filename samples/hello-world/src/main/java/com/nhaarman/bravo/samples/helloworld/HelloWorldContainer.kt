package com.nhaarman.bravo.samples.helloworld

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.RestorableContainer
import kotlinx.android.synthetic.main.hello_world.view.*

/** An interface describing the "Hello, world!" view. */
interface HelloWorldContainer : Container {

    var text: String
}

/**
 * A [View] implementation implementing the [HelloWorldContainer].
 *
 * This implementation does not handle any state restoration, as there is no
 * state worth saving.
 * In cases where _is_ state worth saving, your [Container] should generally
 * implement [RestorableContainer].
 */
class HelloWorldView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), HelloWorldContainer {

    override var text: String = ""
        set(value) {
            textView.text = value
        }
}