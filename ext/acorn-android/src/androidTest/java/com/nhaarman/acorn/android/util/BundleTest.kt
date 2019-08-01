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
