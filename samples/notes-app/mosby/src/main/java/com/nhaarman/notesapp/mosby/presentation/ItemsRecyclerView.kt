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

package com.nhaarman.notesapp.mosby.presentation

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.ViewGroup
import com.nhaarman.notesapp.mosby.R
import com.nhaarman.notesapp.mosby.inflate
import com.nhaarman.notesapp.mosby.note.NoteItem
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.extensions.LayoutContainer

class ItemsRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    var items: List<NoteItem> = emptyList()
        set(new) {
            val old = field
            field = new

            DiffUtil.calculateDiff(object : DiffUtil.Callback() {

                override fun getOldListSize() = old.size
                override fun getNewListSize() = new.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return old[oldItemPosition].id == new[newItemPosition].id
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return old[oldItemPosition] == new[newItemPosition]
                }
            }).dispatchUpdatesTo(adapter!!)
        }

    override fun onFinishInflate() {
        super.onFinishInflate()

        layoutManager = LinearLayoutManager(context)
        adapter = ItemsAdapter()
    }

    private val clicksSubject = PublishSubject.create<NoteItem>()
    val itemClicks: Observable<NoteItem> = clicksSubject

    private val deleteSubject = PublishSubject.create<NoteItem>()
    val deleteClicks: Observable<NoteItem> = deleteSubject

    private inner class ItemsAdapter : RecyclerView.Adapter<NoteViewHolder>() {

        override fun getItemCount() = items.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
            return NoteViewHolder(parent.inflate(R.layout.itemlist_itemview))
        }

        override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
            holder.apply {
                item = items[position]
            }
        }
    }

    private inner class NoteViewHolder(
        override val containerView: ItemView
    ) : ViewHolder(containerView), LayoutContainer {

        var item: NoteItem? = null
            set(value) {
                field = value
                containerView.text = value?.text
            }

        init {
            containerView
                .setOnClickListener { item?.let { clicksSubject.onNext(it) } }

//            deleteButton
//                .setOnClickListener { item?.let { deleteSubject.onNext(it) } }
        }
    }
}
