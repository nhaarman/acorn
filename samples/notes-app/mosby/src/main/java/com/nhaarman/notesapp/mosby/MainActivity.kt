package com.nhaarman.notesapp.mosby

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nhaarman.notesapp.mosby.presentation.ItemListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, ItemListFragment())
            .commit()
    }
}
