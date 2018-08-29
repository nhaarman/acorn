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
