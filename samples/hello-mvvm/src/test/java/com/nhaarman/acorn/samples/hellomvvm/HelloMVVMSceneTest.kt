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

package com.nhaarman.acorn.samples.hellomvvm

import com.nhaarman.expect.expect
import io.reactivex.schedulers.TestScheduler
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

internal class HelloMVVMSceneTest {

    private val testScheduler = TestScheduler()
    private val scene = HelloMVVMScene(testScheduler)

    @Test
    fun `scene data propagates`() {
        /* Given */
        val observer = scene.data.test()

        /* When */
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        /* Then */
        expect(observer.values()).toBe(listOf(0, 1))
    }
}