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

package com.nhaarman.acorn.samples.transitionanimation.album

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nhaarman.acorn.android.util.inflate
import com.nhaarman.acorn.samples.transitionanimation.R
import com.squareup.picasso.Picasso

class AlbumRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var imageUrls: List<String> = emptyList()
        set(value) {
            field = value
            adapter?.notifyDataSetChanged()
        }

    override fun onFinishInflate() {
        super.onFinishInflate()

        layoutManager = GridLayoutManager(context, 3)
        adapter = AlbumAdapter()
    }

    private var listener: (String) -> Unit = {}
    fun setOnImageClickListener(f: (String) -> Unit) {
        listener = f
    }

    fun clickedView(): View? {
        val clickedUrl = clickedState?.url ?: return null

        val clickedPosition = imageUrls.indexOf(clickedUrl)
        return layoutManager!!.findViewByPosition(clickedPosition)
    }

    private var clickedState: ClickedState? = null

    private fun viewClicked(imageUrl: String) {
        clickedState = ClickedState(imageUrl, false)
        listener.invoke(imageUrl)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()

        return Bundle()
            .also {
                it.putParcelable("super_state", superState)
                it.putString("clicked_url", clickedState?.url)
            }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState((state as Bundle).getParcelable("super_state"))

        state.getString("clicked_url")?.let {
            clickedState = ClickedState(it, true)
        }
    }

    private inner class AlbumAdapter : Adapter<ImageViewHolder>() {

        override fun getItemCount(): Int {
            return imageUrls.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            return ImageViewHolder(parent.inflate(R.layout.album_imageitem))
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            holder.imageUrl = imageUrls[position]
        }
    }

    private inner class ImageViewHolder(
        private val imageView: ImageView
    ) : ViewHolder(imageView) {

        init {
            imageView.setOnClickListener {
                imageUrl?.let { viewClicked(it) }
            }
        }

        var imageUrl: String? = null
            set(value) {
                field = value

                if (value == null) {
                    imageView.setImageDrawable(null)
                    return
                }

                // RecyclerView adds a manually removed view automatically, but
                // we want to avoid this after an item was clicked -> make it
                // invisible. After restoration however, the view should remain
                // visible.
                if (clickedState?.fromRestored == false && clickedState?.url == value) {
                    imageView.visibility = View.INVISIBLE
                } else {
                    imageView.visibility = View.VISIBLE
                }

                Picasso.get()
                    .load(value)
                    .into(imageView)
            }
    }

    private class ClickedState(
        val url: String,
        val fromRestored: Boolean
    )
}