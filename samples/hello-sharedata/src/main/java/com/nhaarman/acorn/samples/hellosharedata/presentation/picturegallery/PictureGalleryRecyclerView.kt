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

package com.nhaarman.bravo.samples.hellosharedata.presentation.picturegallery

import android.content.Context
import android.graphics.Bitmap
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.nhaarman.bravo.samples.hellosharedata.R
import com.nhaarman.bravo.samples.hellosharedata.pictures.Picture
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import kotlinx.android.synthetic.main.picturegallery_scene.view.*

class PictureGalleryRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.recyclerview.widget.RecyclerView(context, attrs, defStyleAttr) {

    var pictures: List<Picture> = emptyList()
        set(value) {
            field = value
            picturesRecyclerView.adapter?.notifyDataSetChanged()
        }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (isInEditMode) return

        layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)
        adapter = PicturesAdapter()
    }

    private var listeners = listOf<(Picture) -> Unit>()
    fun addOnPictureSelectedListener(f: (Picture) -> Unit) {
        listeners += f
    }

    private inner class PicturesAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<MyViewHolder>() {

        override fun getItemCount(): Int {
            return pictures.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return LayoutInflater.from(context)
                .inflate(R.layout.picturegallery_picture, parent, false)
                .let { MyViewHolder(it as ImageView) }
        }

        override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
            viewHolder.picture = pictures[position]
        }
    }

    private inner class MyViewHolder(val imageView: ImageView) : androidx.recyclerview.widget.RecyclerView.ViewHolder(imageView) {

        var picture: Picture? = null
            set(value) {
                if (value == null) {
                    imageView.setImageDrawable(null)
                    return
                }

                Picasso.get()
                    .load(value.file)
                    .transform(SquareTransformation(this@PictureGalleryRecyclerView.width / 2))
                    .into(imageView)

                imageView.setOnClickListener {
                    listeners.forEach { listener ->
                        listener.invoke(value)
                    }
                }
            }
    }

    private class SquareTransformation(
        private val maxWidth: Int
    ) : Transformation {

        private var size = 0
        private var x = 0
        private var y = 0

        override fun transform(source: Bitmap): Bitmap {
            size = Math.min(Math.min(source.width, source.height), maxWidth)
            x = (source.width - size) / 2
            y = (source.height - size) / 2

            return Bitmap.createBitmap(source, x, y, size, size)
                .also {
                    if (it != source) {
                        source.recycle()
                    }
                }
        }

        override fun key(): String {
            return "SquareTransformation(size=$size, x=$x, y=$y)"
        }
    }
}