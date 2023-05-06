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

import com.nhaarman.acorn.presentation.SceneKey.Companion.defaultKey
import com.nhaarman.expect.expect
import org.junit.jupiter.api.Test

internal class SceneKeyTest {

    val testSceneName = "com.nhaarman.acorn.presentation.SceneKeyTest\$TestScene"

    @Test
    fun `sceneKey value`() {
        // When
        val key = SceneKey("test")

        // Then
        expect(key.value).toBe("test")
    }

    @Test
    fun `sceneKey equality`() {
        assert(SceneKey("a") == SceneKey("a"))
        assert(SceneKey("a") != SceneKey("b"))
    }

    @Test
    fun `creating key with 'from KClass'`() {
        val actual = SceneKey.from(TestScene::class)
        val expected = SceneKey(testSceneName)

        expect(actual).toBe(expected)
    }

    @Test
    fun `creating key with 'from Class'`() {
        val actual = SceneKey.from(TestScene::class.java)
        val expected = SceneKey(testSceneName)

        expect(actual).toBe(expected)
    }

    @Test
    fun `creating key from default key`() {
        val actual = SceneKey.defaultKey<TestScene>()
        val expected = SceneKey(testSceneName)

        expect(actual).toBe(expected)
    }

    @Test
    fun `creating key from default key extension`() {
        val actual = TestScene().defaultKey()
        val expected = SceneKey(testSceneName)

        expect(actual).toBe(expected)
    }

    @Test
    fun `test toString`() {
        // Given
        val key = SceneKey("test")

        // Then
        expect(key.toString()).toBe("SceneKey(value=test)")
    }

    class TestScene : Scene<Container>
}
