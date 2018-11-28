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

package com.nhaarman.circleci

import android.app.Application
import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers

internal val Context.navigatorProvider get() = (applicationContext as CircleCIApplication).navigatorProvider

class CircleCIApplication : Application() {

    private val module by lazy {
        CircleCIModule()
    }

    internal val navigatorProvider by lazy {
        CircleCINavigatorProvider(module.component)
    }

    override fun onCreate() {
        mainThread = AndroidSchedulers.mainThread()
        super.onCreate()
    }
}