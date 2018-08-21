package com.nhaarman.notesapp.conductor.presentation.createitem

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.rxlifecycle2.ControllerEvent
import com.bluelinelabs.conductor.rxlifecycle2.RxController
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import com.nhaarman.notesapp.conductor.R
import com.nhaarman.notesapp.conductor.noteAppComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.controller_createitem.view.*

class CreateItemController : RxController() {

    private val noteItemsRepository get() = activity!!.noteAppComponent.noteItemsRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_createitem, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onAttach(view: View) {
        RxToolbar.itemClicks(view.createItemToolbar)
            .filter { it.itemId == R.id.save }
            .map { view.editText.text.toString() }
            .compose(bindUntilEvent(ControllerEvent.DETACH))
            .firstElement()
            .flatMapSingle { text -> noteItemsRepository.create(text) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _ -> router.popCurrentController() }
    }
}