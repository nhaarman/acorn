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

package com.nhaarman.acorn.notesapp

import io.reactivex.Scheduler
import io.reactivex.internal.schedulers.ImmediateThinScheduler
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext

class ImmediateMainThreadExtension : Extension,
    BeforeAllCallback,
    AfterAllCallback {

    private val ExtensionContext.store get() = getStore(ExtensionContext.Namespace.create(this))

    override fun beforeAll(context: ExtensionContext) {
        context.store.put("original", mainThread)
        mainThread = ImmediateThinScheduler.INSTANCE
    }

    override fun afterAll(context: ExtensionContext) {
        mainThread = context.store.get("original", Scheduler::class.java)
    }
}