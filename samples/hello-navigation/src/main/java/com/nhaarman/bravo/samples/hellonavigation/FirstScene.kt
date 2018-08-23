package com.nhaarman.bravo.samples.hellonavigation

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.samples.hellonavigation.FirstScene.Events
import kotlinx.android.synthetic.main.first_scene.view.*

/**
 * A Scene that shows a button to navigate to the second Scene.
 *
 * This Scene exposes the [FirstScene.Events] callback interface to let the
 * Navigator know the second Scene is requested.
 *
 * The [FirstSceneContainer] has a function that accepts a callback when the
 * button is clicked. When the container is attached to the Scene, it registers
 * its callback with the container. Upon a button click, the [Events] callback
 * is invoked and the Navigator takes care of the rest.
 */
class FirstScene(
    /** The listener callback to be notified when an event happens. */
    private val listener: Events
) : Scene<FirstSceneContainer> {

    override fun attach(v: FirstSceneContainer) {
        v.onSecondSceneClicked { listener.secondSceneRequested() }
    }

    /**
     * An interface to let users of this Scene know something happened.
     */
    interface Events {

        fun secondSceneRequested()
    }
}

interface FirstSceneContainer : Container {

    fun onSecondSceneClicked(f: () -> Unit)
}

class FirstSceneView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), FirstSceneContainer {

    override fun onSecondSceneClicked(f: () -> Unit) {
        secondSceneButton.setOnClickListener { f() }
    }
}