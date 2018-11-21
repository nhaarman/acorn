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

import com.nhaarman.acorn.presentation.MVVMScene
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class HelloMVVMScene(
    private val mainThreadScheduler: Scheduler = AndroidSchedulers.mainThread()
) : MVVMScene {

    val data: Observable<Long> by lazy {
        Observable.interval(0, 1, TimeUnit.SECONDS, mainThreadScheduler)
            .replay(1).autoConnect(1) { d -> disposable = d }
    }

    private var disposable: Disposable? = null
        set(value) {
            field?.dispose()
            field = value
        }

    override fun onDestroy() {
        disposable = null
    }
}