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

package com.nhaarman.circleci.dashboard.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nhaarman.acorn.android.util.inflate
import com.nhaarman.circleci.Build
import com.nhaarman.circleci.ui.R
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.dashboard_recentbuild.*

class RecentBuildsRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var recentBuilds: List<Build> = emptyList()
        set(new) {
            val old = field
            field = new

            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return old.size
                }

                override fun getNewListSize(): Int {
                    return new.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return old[oldItemPosition].buildNumber == new[newItemPosition].buildNumber
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return old[oldItemPosition] == new[newItemPosition]
                }
            }).dispatchUpdatesTo(adapter!!)
        }

    override fun onFinishInflate() {
        super.onFinishInflate()

        layoutManager = LinearLayoutManager(context)
        adapter = RecentBuildsAdapter()

        addItemDecoration(object : ItemDecoration() {

            val spacing by lazy { resources.getDimensionPixelSize(R.dimen.dashboard_element_spacing) }

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.bottom += spacing
            }
        })
    }

    private inner class RecentBuildsAdapter : Adapter<RecentBuildViewHolder>() {

        override fun getItemCount(): Int {
            return recentBuilds.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentBuildViewHolder {
            return RecentBuildViewHolder(parent.inflate(R.layout.dashboard_recentbuild))
        }

        override fun onBindViewHolder(holder: RecentBuildViewHolder, position: Int) {
            holder.build = recentBuilds[position]
        }
    }

    inner class RecentBuildViewHolder(
        override val containerView: View
    ) : ViewHolder(containerView), LayoutContainer {

        var build: Build? = null
            set(value) {
                if (value == null) return

                nameTV.text = "${value.repoName} / ${value.branchName} / #${value.buildNumber}"
                statusView.status = value.status
                avatarUrl = value.user.avatarUrl
                subjectTV.text = value.subject
            }

        private var avatarUrl: String? = null
            set(value) {
                avatarIV.isVisible = value != null

                if (value == null) {
                    return
                }

                Picasso.get()
                    .load(value)
                    .into(avatarIV)
            }
    }
}