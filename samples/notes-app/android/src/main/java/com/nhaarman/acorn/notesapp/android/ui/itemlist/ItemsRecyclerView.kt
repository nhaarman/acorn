/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.acorn.notesapp.android.ui.itemlist

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.nhaarman.acorn.android.util.inflate
import com.nhaarman.acorn.notesapp.android.R
import com.nhaarman.acorn.notesapp.note.NoteItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.itemlist_itemview.*

class ItemsRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int = 0
) : androidx.recyclerview.widget.RecyclerView(context, attrs, defStyle) {

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

    var clickedView: View? = null
        private set

    override fun onFinishInflate() {
        super.onFinishInflate()

        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        adapter = ItemsAdapter()
    }

    private var listeners = listOf<ClicksListener>()
    fun addClicksListener(listener: ClicksListener) {
        listeners += listener
    }

    fun removeClicksListener(listener: ClicksListener) {
        listeners -= listener
    }

    private inner class ItemsAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<NoteViewHolder>() {

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
            containerView.setOnClickListener { view ->
                item?.let {
                    clickedView = view
                    listeners.forEach { listener -> listener.onItemClicked(it) }
                }
            }

            deleteButton.setOnClickListener { _ ->
                item?.let {
                    listeners.forEach { listener -> listener.onDeleteClicked(it) }
                }
            }
        }
    }

    interface ClicksListener {

        fun onItemClicked(item: NoteItem)
        fun onDeleteClicked(item: NoteItem)
    }
}
