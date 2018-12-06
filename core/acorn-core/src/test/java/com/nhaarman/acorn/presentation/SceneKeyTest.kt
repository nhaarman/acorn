/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.acorn.presentation

import com.nhaarman.acorn.presentation.SceneKey.Companion.defaultKey
import com.nhaarman.expect.expect
import org.junit.jupiter.api.Test

internal class SceneKeyTest {

    val testSceneName = "com.nhaarman.acorn.presentation.SceneKeyTest\$TestScene"

    @Test
    fun `sceneKey value`() {
        /* When */
        val key = SceneKey("test")

        /* Then */
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
        /* Given */
        val key = SceneKey("test")

        /* Then */
        expect(key.toString()).toBe("SceneKey(value=test)")
    }

    class TestScene : Scene<Container>
}