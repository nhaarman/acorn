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

package com.nhaarman.acorn.samples.hellobottombar.places

import android.view.View
import androidx.annotation.CheckResult
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.navigation.DisposableHandle
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.samples.hellobottombar.DestinationSelectedListener
import com.nhaarman.acorn.samples.hellobottombar.R
import com.nhaarman.acorn.samples.hellobottombar.util.setDestinationSelectedListener

interface PlacesContainer : Container {

    @CheckResult
    fun setDestinationSelectedListener(listener: DestinationSelectedListener): DisposableHandle
}

class PlacesViewController(
    override val view: View,
) : ViewController, PlacesContainer {

    init {
        view
            .findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .selectedItemId = R.id.places
    }

    override fun setDestinationSelectedListener(listener: DestinationSelectedListener): DisposableHandle {
        return view.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setDestinationSelectedListener(listener)
    }
}
