/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
