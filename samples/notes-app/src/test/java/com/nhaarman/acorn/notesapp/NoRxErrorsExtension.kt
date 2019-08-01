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
