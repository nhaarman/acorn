package com.nhaarman.bravo.samples.helloworld

import android.app.Activity
import android.os.Bundle
import com.nhaarman.bravo.android.BravoActivityDelegate
import com.nhaarman.bravo.android.transition.DefaultTransitionFactory
import com.nhaarman.bravo.android.transition.bindViews

class MainActivity : Activity() {

    private val delegate by lazy {
        val viewFactory = bindViews {
            bind(HelloWorldScene.key, R.layout.hello_world)
        }

        BravoActivityDelegate(
            this,
            HelloWorldNavigatorProvider,
            viewFactory,
            DefaultTransitionFactory(viewFactory)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        delegate.onStart()
    }

    override fun onStop() {
        super.onStop()
        delegate.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        delegate.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        delegate.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (!delegate.onBackPressed()) {
            super.onBackPressed()
        }
    }
}