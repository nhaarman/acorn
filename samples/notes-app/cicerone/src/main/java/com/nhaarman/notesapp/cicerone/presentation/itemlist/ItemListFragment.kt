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