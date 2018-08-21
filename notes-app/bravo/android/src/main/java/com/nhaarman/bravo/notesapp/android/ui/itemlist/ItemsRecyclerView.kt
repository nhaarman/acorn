package com.nhaarman.bravo.notesapp.android.ui.itemlist

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.ViewGroup
import com.nhaarman.bravo.android.util.inflate
import com.nhaarman.bravo.notesapp.android.R
import com.nhaarman.bravo.notesapp.note.NoteItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.itemlist_itemview.*

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

    private var listeners = listOf<ClicksListener>()
    fun addClicksListener(listener: ClicksListener) {
        listeners += listener
    }

    fun removeClicksListener(listener: ClicksListener) {
        listeners -= listener
    }

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
            containerView.setOnClickListener { _ ->
                item?.let {
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
