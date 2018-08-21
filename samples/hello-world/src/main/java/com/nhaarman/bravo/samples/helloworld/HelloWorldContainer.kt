package com.nhaarman.bravo.samples.helloworld

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.nhaarman.bravo.presentation.Container
import kotlinx.android.synthetic.main.hello_world.view.*

interface HelloWorldContainer : Container {

    var text: String
}

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