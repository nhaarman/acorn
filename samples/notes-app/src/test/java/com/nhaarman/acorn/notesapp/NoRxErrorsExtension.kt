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

import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext

@Suppress("UNCHECKED_CAST")
class NoRxErrorsExtension : Extension,
    BeforeTestExecutionCallback,
    AfterTestExecutionCallback {

    private val ExtensionContext.store get() = getStore(ExtensionContext.Namespace.create(this))

    override fun beforeTestExecution(context: ExtensionContext) {
        context.store.put("original", RxJavaPlugins.getErrorHandler())
        RxJavaPlugins.setErrorHandler {
            context.store.put("throwable", it)
        }
    }

    override fun afterTestExecution(context: ExtensionContext) {
        RxJavaPlugins.setErrorHandler(context.store.get("original") as? Consumer<in Throwable>)
        (context.store.get("throwable") as? Throwable)?.let {
            throw it
        }
    }
}