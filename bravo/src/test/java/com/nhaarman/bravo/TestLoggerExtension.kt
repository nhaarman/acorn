package com.nhaarman.bravo

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext

class TestLoggerExtension : Extension, BeforeAllCallback, AfterAllCallback {

    override fun beforeAll(context: ExtensionContext) {
        context.store.put("original", logger)
        logger = TestLogger()
    }

    override fun afterAll(context: ExtensionContext) {
        logger = context.store.get("original") as? Logger
    }

    private val ExtensionContext.store get() = getStore(ExtensionContext.Namespace.create(this))

    private class TestLogger : Logger {

        override fun v(tag: String, message: Any?) {
            println("v [$tag]: $message")
        }

        override fun d(tag: String, message: Any?) {
            println("d [$tag]: $message")
        }

        override fun i(tag: String, message: Any?) {
            println("v [$tag]: $message")
        }

        override fun w(tag: String, message: Any?) {
            System.err.println("w [$tag]: $message")
        }

        override fun e(tag: String, message: Any?) {
            System.err.println("w [$tag]: $message")
        }
    }
}