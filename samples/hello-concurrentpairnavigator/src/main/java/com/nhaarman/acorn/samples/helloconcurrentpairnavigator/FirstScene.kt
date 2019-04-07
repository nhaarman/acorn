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

package com.nhaarman.acorn.samples.helloconcurrentpairnavigator

import android.view.View
import android.view.ViewGroup
import com.nhaarman.acorn.android.presentation.ProvidesView
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.util.inflate
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.RxScene
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.first_scene.*
import java.util.concurrent.TimeUnit

class FirstScene(
    private val listener: Events,
    scheduler: Scheduler = AndroidSchedulers.mainThread()
) : RxScene<FirstSceneContainer>(null), ProvidesView {

    private val counter = Observable
        .interval(0, 100, TimeUnit.MILLISECONDS, scheduler)
        .replay(1).autoConnect(this)

    override fun createViewController(parent: ViewGroup): ViewController {
        return FirstSceneViewController(parent.inflate(R.layout.first_scene))
    }

    override fun onStart() {
        super.onStart()

        disposables += counter
            .combineWithLatestView()
            .subscribe { (count, container) ->
                container?.count = count
            }
    }

    override fun attach(v: FirstSceneContainer) {
        super.attach(v)
        v.onActionClicked { listener.actionClicked() }
    }

    interface Events {

        fun actionClicked()
    }
}

interface FirstSceneContainer : Container {

    var count: Long

    fun onActionClicked(f: () -> Unit)
}

class FirstSceneViewController(
    override val view: View
) : RestorableViewController, FirstSceneContainer {

    override var count: Long = 0
        set(value) {
            counterTV.text = value.toString()
        }

    override fun onActionClicked(f: () -> Unit) {
        secondSceneButton.setOnClickListener { f() }
    }
}
