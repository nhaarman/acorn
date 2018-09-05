@file:Suppress("UNCHECKED_CAST")

package com.nhaarman.bravo.testing

import com.nhaarman.bravo.OnBackPressListener
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.navigation.TransitionData
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

/**
 * A class that provides simple Scene/Container management for tests.
 */
class TestContext private constructor(
    private val navigator: Navigator<Navigator.Events>,
    private val containerProvider: ContainerProvider
) : Navigator.Events {

    private var scene: Scene<out Container>? = null
    private var container: Container? = null
    fun <T> container(): T = container as T

    init {
        navigator.addListener(this)
        navigator.onStart()
    }

    override fun scene(scene: Scene<out Container>, data: TransitionData?) {
        (this.scene as? Scene<Container>)?.detach(container!!)
        this.scene = scene
        this.container = containerProvider.containerFor(scene).also { (scene as Scene<Container>).attach(it) }
    }

    var finished = false
    override fun finished() {
        finished = true
    }

    fun onBackPressed() {
        (navigator as OnBackPressListener).onBackPressed()
    }

    companion object {

        fun create(navigator: Navigator<*>, containerProvider: ContainerProvider): TestContext {
            return TestContext(navigator as Navigator<Navigator.Events>, containerProvider)
        }
    }
}