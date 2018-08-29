package com.nhaarman.bravo.android.util

import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray
import androidx.core.util.set
import com.nhaarman.bravo.state.navigatorState
import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.state.sceneState
import com.nhaarman.bravo.state.containerState
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
            it["transformToBravo"] = 3.14
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
            it["transformToBravo"] = 3.14
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
            it["transformToBravo"] = 3.14
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
        array[0] = MyParcelable(3)

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
            it["transformToBravo"] = 3.14
            it["scene"] = sceneState {
                it["bar"] = 42
                it["container"] = containerState {
                    it["baz"] = 1337
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