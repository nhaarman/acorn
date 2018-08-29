package com.nhaarman.bravo.notesapp

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