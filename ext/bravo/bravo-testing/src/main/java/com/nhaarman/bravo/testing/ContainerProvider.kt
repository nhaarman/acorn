package com.nhaarman.bravo.testing

import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

/**
 * An interface that can provide [Container] instances for [Scene]s when working
 * with [TestContext].
 */
interface ContainerProvider {

    fun containerFor(scene: Scene<*>): Container
}