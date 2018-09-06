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

package com.nhaarman.notesapp.cicerone.presentation.itemlist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nhaarman.notesapp.cicerone.R
import com.nhaarman.notesapp.cicerone.application
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_itemlist.*

class ItemListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_itemlist, container, false)
    }

    private val disposables = CompositeDisposable()
    private var itemsDisposable: Disposable? = null
        set(value) {
            field?.dispose()
            field = value
        }

    private val items by lazy {
        requireContext().application.noteAppComponent.noteItemsRepository
            .noteItems
            .observeOn(AndroidSchedulers.mainThread())
            .replay(1).autoConnect(1) { itemsDisposable = it }
    }

    override fun onStart() {
        super.onStart()
        disposables += items.subscribe { itemsRecyclerView.items = it }

        disposables += itemsRecyclerView.itemClicks
            .subscribe {
                context?.application?.cicerone?.router?.navigateTo("edit_item", it)
            }
    }

    override fun onStop() {
        disposables.clear()
        super.onStop()
    }

    override fun onDestroy() {
        itemsDisposable = null
        super.onDestroy()
    }
}