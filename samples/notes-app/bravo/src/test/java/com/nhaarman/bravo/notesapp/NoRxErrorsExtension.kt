package com.nhaarman.bravo.notesapp

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