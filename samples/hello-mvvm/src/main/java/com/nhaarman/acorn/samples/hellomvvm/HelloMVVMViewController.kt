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

import android.annotation.SuppressLint
import android.view.View
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.presentation.MVVMContainer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.hello_mvvm.*

class HelloMVVMViewController(
    override val view: View
) : MVVMContainer<HelloMVVMScene>, RestorableViewController {

    private var disposable: Disposable? = null
        set(value) {
            field?.dispose()
            field = value
        }

    @SuppressLint("SetTextI18n")
    override fun attachTo(scene: HelloMVVMScene) {
        disposable = scene.data.subscribe { textView.text = "Hello: $it" }
    }

    override fun detachFrom(scene: HelloMVVMScene) {
        disposable = null
    }
}