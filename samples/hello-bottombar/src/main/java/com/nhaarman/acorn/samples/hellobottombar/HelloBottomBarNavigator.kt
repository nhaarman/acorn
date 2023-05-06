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

package com.nhaarman.acorn.samples.hellobottombar

import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.navigation.SavableNavigator
import com.nhaarman.acorn.navigation.experimental.BottomBarNavigator
import com.nhaarman.acorn.navigation.experimental.ExperimentalCompositeParallelNavigator
import com.nhaarman.acorn.samples.hellobottombar.MyDestination.Favorites
import com.nhaarman.acorn.samples.hellobottombar.MyDestination.Music
import com.nhaarman.acorn.samples.hellobottombar.MyDestination.Places
import com.nhaarman.acorn.samples.hellobottombar.MyDestination.values
import com.nhaarman.acorn.samples.hellobottombar.favorites.FavoritesNavigator
import com.nhaarman.acorn.samples.hellobottombar.music.MusicNavigator
import com.nhaarman.acorn.samples.hellobottombar.news.NewsNavigator
import com.nhaarman.acorn.samples.hellobottombar.places.PlacesNavigator
import com.nhaarman.acorn.state.NavigatorState

@OptIn(ExperimentalCompositeParallelNavigator::class)
class HelloBottomBarNavigator(
    savedState: NavigatorState?,
) : BottomBarNavigator<MyDestination>(Favorites, savedState),
    SavableNavigator {

    override fun serialize(destination: MyDestination): String {
        return "${destination.ordinal}"
    }

    override fun deserialize(serializedDestination: String): MyDestination {
        return values()[serializedDestination.toInt()]
    }

    override fun createNavigator(destination: MyDestination, savedState: NavigatorState?): Navigator {
        return when (destination) {
            Favorites -> FavoritesNavigator(
                object : FavoritesNavigator.Events {
                    override fun onDestinationSelected(destination: MyDestination) {
                        select(destination)
                    }
                },
                savedState,
            )
            Music -> MusicNavigator(
                object : MusicNavigator.Events {
                    override fun onDestinationSelected(destination: MyDestination) {
                        select(destination)
                    }
                },
                savedState,
            )
            Places -> PlacesNavigator(
                object : PlacesNavigator.Events {
                    override fun onDestinationSelected(destination: MyDestination) {
                        select(destination)
                    }
                },
                savedState,
            )
            MyDestination.News -> NewsNavigator(
                object : NewsNavigator.Events {
                    override fun onDestinationSelected(destination: MyDestination) {
                        select(destination)
                    }
                },
                savedState,
            )
        }
    }
}
