/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.notesapp.cicerone

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.nhaarman.notesapp.cicerone.presentation.edititem.EditItemFragment
import com.nhaarman.notesapp.cicerone.presentation.itemlist.ItemListFragment
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.android.SupportFragmentNavigator
import ru.terrakok.cicerone.commands.Replace

class MainActivity : AppCompatActivity() {

    private val navigatorHolder by lazy {
        cicerone.navigatorHolder
    }

    private val navigator: Navigator by lazy {
        object : SupportFragmentNavigator(supportFragmentManager, android.R.id.content) {

            override fun exit() {
                finish()
            }

            override fun createFragment(screenKey: String, data: Any?): Fragment {
                return when (screenKey) {
                    "item_list" -> ItemListFragment()
                    "edit_item" -> EditItemFragment()
                    else -> error("Unknown screen key: $screenKey")
                }
            }

            override fun showSystemMessage(message: String?) {
                error("Not used")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            navigator.applyCommands(arrayOf(Replace("item_list", null)))
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }
}
