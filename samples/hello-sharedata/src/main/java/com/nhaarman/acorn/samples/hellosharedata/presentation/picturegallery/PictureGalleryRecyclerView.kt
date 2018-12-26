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

package com.nhaarman.acorn.samples.hellosharedata.presentation.picturegallery

import android.content.Context
import android.graphics.Bitmap
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.nhaarman.acorn.samples.hellosharedata.R
import com.nhaarman.acorn.samples.hellosharedata.pictures.Picture
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