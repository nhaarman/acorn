/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nhaarman.acorn.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class SceneKeyTest {

    val testSceneName = "com.nhaarman.acorn.presentation.SceneKeyTest\$TestScene"

    @Test
    fun sceneKeyValue() {
        /* When */
        val key = SceneKey("test")

        /* Then */
        assertEquals("test", key.value)
    }

    @Test
    fun sceneKeyEquality() {
        assertEquals(SceneKey("a"), SceneKey("a"))
        assertNotEquals(SceneKey("a"), SceneKey("b"))
    }

//    @Test
//    fun creatingKeyFromKClass() {
//        val actual = SceneKey.from(TestScene::class)
//        val expected = SceneKey(testSceneName)
//
//        assertEquals(expected, actual)
//    }
//
//    @Test
//    fun creatingKeyFromDefaultKey() {
//        val actual = SceneKey.defaultKey<TestScene>()
//        val expected = SceneKey(testSceneName)
//
//        assertEquals(expected, actual)
//    }
//
//    @Test
//    fun creatingKeyFromDefaultKeyExtension() {
//        val actual = TestScene().defaultKey()
//        val expected = SceneKey(testSceneName)
//
//        assertEquals(expected, actual)
//    }

    @Test
    fun testToString() {
        /* Given */
        val key = SceneKey("test")

        /* Then */
        assertEquals("SceneKey(value=test)", key.toString())
    }

    class TestScene : Scene<Container> {
        override val key: SceneKey
            get() = SceneKey("test")
    }
}
