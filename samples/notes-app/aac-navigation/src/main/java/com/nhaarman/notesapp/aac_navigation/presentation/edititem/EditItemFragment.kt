package com.nhaarman.notesapp.aac_navigation.presentation.edititem

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import com.nhaarman.notesapp.aac_navigation.R
import com.nhaarman.notesapp.aac_navigation.noteAppComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_edititem.view.*

class EditItemFragment : Fragment() {

    private val itemId get() = EditItemFragmentArgs.fromBundle(arguments).itemId.toLong()

    private val noteItemsRepository get() = activity!!.noteAppComponent.noteItemsRepository

    private val originalItem by lazy {
        noteItemsRepository.find(itemId)
            .observeOn(AndroidSchedulers.mainThread())
            .replay(1).refCount()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edititem, container, false)
    }

    private val viewDisposables = CompositeDisposable()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            viewDisposables += originalItem
                .firstElement()
                .subscribe {
                    getView()?.editText?.setText(it.orNull()?.text)
                }
        }

        val menuClicks = RxToolbar.itemClicks(view.editItemToolbar)
            .share()

        viewDisposables += menuClicks
            .filter { it.itemId == R.id.save }
            .map { view.editText.text.toString() }
            .flatMapSingle { text -> noteItemsRepository.update(itemId, text) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _ ->
                findNavController(this).popBackStack()
            }

        viewDisposables += menuClicks
            .filter { it.itemId == R.id.delete }
            .subscribe {
                noteItemsRepository.delete(itemId)
                findNavController(this).popBackStack()
            }
    }

    override fun onDestroyView() {
        viewDisposables.clear()
        super.onDestroyView()
    }
}