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

package com.nhaarman.acorn.samples.hellobottombar.util

import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nhaarman.acorn.navigation.DisposableHandle
import com.nhaarman.acorn.samples.hellobottombar.DestinationSelectedListener
import com.nhaarman.acorn.samples.hellobottombar.MyDestination
import com.nhaarman.acorn.samples.hellobottombar.R

fun BottomNavigationView.setDestinationSelectedListener(listener: DestinationSelectedListener): DisposableHandle {
    val myListener: (MenuItem) -> Boolean = { item ->
        listener.onDestinationSelected(
            when (item.itemId) {
                R.id.favorites -> MyDestination.Favorites
                R.id.music -> MyDestination.Music
                R.id.places -> MyDestination.Places
                R.id.news -> MyDestination.News
                else -> error("Invalid item: $item")
            }
        )
        true
    }

    setOnNavigationItemSelectedListener(myListener)

    return DisposableHandle {
        setOnNavigationItemSelectedListener(null)
    }
}