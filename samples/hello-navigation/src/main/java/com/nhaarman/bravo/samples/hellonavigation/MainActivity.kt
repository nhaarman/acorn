package com.nhaarman.bravo.samples.hellonavigation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nhaarman.bravo.android.BravoActivityDelegate
import com.nhaarman.bravo.android.transition.DefaultTransitionFactory
import com.nhaarman.bravo.android.transition.bindViews
import com.nhaarman.bravo.presentation.SceneKey.Companion.defaultKey

class MainActivity : AppCompatActivity() {

    private val delegate by lazy {
        val viewFactory = bindViews {
            bind(defaultKey<FirstScene>(), R.layout.first_scene)
            bind(defaultKey<SecondScene>(), R.layout.second_scene)
        }

        BravoActivityDelegate(
            this,
            HelloNavigationNavigatorProvider,
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