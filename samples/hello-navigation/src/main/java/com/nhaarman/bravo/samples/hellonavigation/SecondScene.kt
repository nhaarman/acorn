package com.nhaarman.bravo.samples.hellonavigation

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import kotlinx.android.synthetic.main.second_scene.view.*

class SecondScene(
    private val listener: Events
) : Scene<SecondSceneContainer> {

    override val key = Companion.key

    override fun attach(v: SecondSceneContainer) {
        v.onFirstSceneClicked { listener.onFirstSceneRequested() }
    }

    interface Events {

        fun onFirstSceneRequested()
    }

    companion object {

        val key = SecondScene::class.java.name
    }
}

interface SecondSceneContainer : Container {

    fun onFirstSceneClicked(f: () -> Unit)
}

class SecondSceneView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), SecondSceneContainer {

    override fun onFirstSceneClicked(f: () -> Unit) {
        firstSceneButton.setOnClickListener { f() }
    }
}