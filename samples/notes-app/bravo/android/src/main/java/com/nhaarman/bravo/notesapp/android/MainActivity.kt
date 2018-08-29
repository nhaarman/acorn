package com.nhaarman.bravo.notesapp.android

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nhaarman.bravo.android.BravoActivityDelegate
import com.nhaarman.bravo.notesapp.android.TransitionFactoryProvider.transitionFactory
import com.nhaarman.bravo.notesapp.android.ViewFactoryProvider.viewFactory

class MainActivity : AppCompatActivity() {

    private val delegate by lazy {
        BravoActivityDelegate(
            this,
            notesApplication.navigatorProvider,
            viewFactory,
            transitionFactory
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

    override fun onBackPressed() {
        val handled = delegate.onBackPressed()
        if (!handled) super.onBackPressed()
    }

    override fun onStop() {
        super.onStop()
        delegate.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        delegate.onDestroy()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        delegate.onSaveInstanceState(outState)
    }
}