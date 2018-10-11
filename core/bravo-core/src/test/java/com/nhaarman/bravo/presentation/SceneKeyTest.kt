package com.nhaarman.bravo.presentation

import org.junit.jupiter.api.Test

internal class SceneKeyTest {

    @Test
    fun sceneKeyEquality() {
        assert(SceneKey("a") == SceneKey("a"))
        assert(SceneKey("a") != SceneKey("b"))
    }
}