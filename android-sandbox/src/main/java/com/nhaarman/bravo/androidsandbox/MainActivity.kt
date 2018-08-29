package com.nhaarman.bravo.androidsandbox

import android.app.Activity
import android.os.Bundle
import com.nhaarman.bravo.SceneState
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.navigation.SingleSceneNavigator
import com.nhaarman.bravo.navigation.plusAssign
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber.i

class MyScene : Scene<Container> {

    override fun onStart() {
        i("onStart")
    }

    override fun onStop() {
        i("onStop")
    }

    override fun onDestroy() {
        i("onDestroy")
    }
}

class MyNavigator : SingleSceneNavigator<Navigator.Events>(null) {

    override fun createScene(state: SceneState?): Scene<out Container> = MyScene()
}

val navigator by lazy {

    MyNavigator().also {
        it.onStart()
    }
}

class MainActivity : Activity(), Navigator.Events {

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        disposables += navigator.addListener(this)
    }

    override fun scene(scene: Scene<out Container>) {
        println("Scene: $scene")
    }

    override fun finished() {
        println("Finished")
        finish()
    }

    override fun onBackPressed() {
        navigator.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()

        disposables.clear()
    }
}