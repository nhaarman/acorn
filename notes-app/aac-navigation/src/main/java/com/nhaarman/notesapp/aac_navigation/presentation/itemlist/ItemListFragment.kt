package com.nhaarman.notesapp.aac_navigation.presentation.itemlist

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.jakewharton.rxbinding2.view.clicks
import com.nhaarman.notesapp.aac_navigation.R
import com.nhaarman.notesapp.aac_navigation.note.NoteItemsRepository
import com.nhaarman.notesapp.aac_navigation.noteAppComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_itemlist.view.*

class ItemListFragment : Fragment() {

    lateinit var noteItemsRepository: NoteItemsRepository

    private var itemsDisposable: Disposable? = null
    private val items by lazy {
        noteItemsRepository.noteItems
            .observeOn(AndroidSchedulers.mainThread())
            .replay(1)
            .autoConnect(1) { disposable ->
                itemsDisposable = disposable
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        noteItemsRepository = context.noteAppComponent.noteItemsRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_itemlist, container, false)
    }

    private val viewDisposables = CompositeDisposable()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDisposables += view.createButton
            .clicks()
            .subscribe {
                findNavController(this).navigate(R.id.action_itemListFragment_to_createItemFragment)
            }

        viewDisposables += view.itemsRecyclerView
            .itemClicks
            .subscribe {
                findNavController(this).navigate(
                    ItemListFragmentDirections.actionItemListFragmentToEditItemFragment(it.id.toString())
                )
            }

        viewDisposables += view.itemsRecyclerView
            .deleteClicks
            .subscribe {
                noteItemsRepository.delete(it)
            }
    }

    private val startStopDisposables = CompositeDisposable()
    override fun onStart() {
        super.onStart()

        startStopDisposables += items
            .subscribe { items ->
                view?.itemsRecyclerView?.items = items
            }
    }

    override fun onStop() {
        startStopDisposables.clear()
        super.onStop()
    }

    override fun onDestroyView() {
        viewDisposables.clear()
        super.onDestroyView()
    }
}
