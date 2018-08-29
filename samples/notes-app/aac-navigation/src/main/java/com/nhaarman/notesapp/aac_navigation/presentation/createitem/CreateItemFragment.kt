package com.nhaarman.notesapp.aac_navigation.presentation.createitem

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
import kotlinx.android.synthetic.main.fragment_createitem.view.*

class CreateItemFragment : Fragment() {

    private val noteItemsRepository get() = activity!!.noteAppComponent.noteItemsRepository

    private val initialText: String? get() = CreateItemFragmentArgs.fromBundle(arguments).text

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_createitem, container, false)
    }

    private val viewDisposables = CompositeDisposable()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewDisposables += RxToolbar.itemClicks(view.createItemToolbar)
            .filter { it.itemId == R.id.save }
            .map { view.editText.text.toString() }
            .firstElement()
            .flatMapSingle { text -> noteItemsRepository.create(text) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _ ->
                findNavController(this).popBackStack()
            }
    }

    override fun onDestroyView() {
        viewDisposables.clear()
        super.onDestroyView()
    }
}