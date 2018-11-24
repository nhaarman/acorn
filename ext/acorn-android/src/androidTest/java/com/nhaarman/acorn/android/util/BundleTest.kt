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

package com.nhaarman.acorn.android.util

import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.containerState
import com.nhaarman.acorn.state.navigatorState
import com.nhaarman.acorn.state.savedState
import com.nhaarman.acorn.state.sceneState
import com.nhaarman.expect.expect
import org.junit.Test

@Suppress("NestedLambdaShadowedImplicitParameter")
class BundleTest {

    @Test
    fun toAndFromBundle_empty() {
        /* Given */
        val state = NavigatorState()

        /* When */
        val result = state.toBundle().toNavigatorState()

        /* Then */
        expect(result).toBe(state)
    }

    @Test
    fun toAndFromBundle_numberValue() {
        /* Given */
        val state = navigatorState {
            it["transformToAcorn"] = 3.14
        }

        /* When */
        val result = state.toBundle().toNavigatorState()

        /* Then */
        expect(result).toBe(state)
    }

    @Test
    fun toAndFromBundle_multipleKeys() {
        /* Given */
        val state = navigatorState {
            it["key1"] = 3.14
            it["key2"] = "test"
        }

        /* When */
        val result = state.toBundle().toNavigatorState()

        /* Then */
        expect(result).toBe(state)
    }

    @Test
    fun toAndFromBundle_withSceneState() {
        /* Given */
        val state = navigatorState {
            it["transformToAcorn"] = 3.14
            it["scene"] = sceneState {
                it["bar"] = 42
            }
        }

        /* When */
        val result = state.toBundle().toNavigatorState()

        /* Then */
        expect(result).toBe(state)
    }

    @Test
    fun toAndFromBundle_withContainerState() {
        /* Given */
        val state = navigatorState {
            it["transformToAcorn"] = 3.14
            it["scene"] = sceneState {
                it["bar"] = 42
                it["container"] = containerState {
                    it["baz"] = 1337
                }
            }
        }

        /* When */
        val result = state.toBundle().toNavigatorState()

        /* Then */
        expect(result).toBe(state)
    }

    @Test
    fun toAndFromBundle_sparseParcelableArray() {
        /* Given */
        val array = SparseArray<Parcelable>(3)
        array.put(0, MyParcelable(3))

        val state = navigatorState {
            it.setUnchecked("array", array)
        }

        /* When */
        val result = state.toBundle().toNavigatorState()

        /* Then */
        expect(result).toBe(state)
    }

    @Test
    fun fullBundle_toParcelAndBack() {
        /* Given */
        val state = navigatorState {
            it["transformToAcorn"] = 3.14
            it["scene"] = sceneState {
                it["bar"] = 42
                it["container"] = containerState {
                    it["baz"] = 1337
                    it["state"] = savedState {
                        it["foo"] = 9
                    }
                }
            }
        }

        /* When */
        val parcel = Parcel.obtain()
        parcel.writeBundle(state.toBundle())
        parcel.setDataPosition(0)
        val result = parcel.readBundle()?.toNavigatorState()

        /* Then */
        expect(result).toBe(state)
    }

    class MyParcelable(val value: Int) : Parcelable {

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeInt(value)
        }

        override fun describeContents() = 0
    }
}