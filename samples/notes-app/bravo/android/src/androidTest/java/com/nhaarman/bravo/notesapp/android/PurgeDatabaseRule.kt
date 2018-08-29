package com.nhaarman.bravo.notesapp.android

import android.support.test.InstrumentationRegistry
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class PurgeDatabaseRule : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                InstrumentationRegistry.getTargetContext().noteAppComponent.noteItemsRepository.purge()
                base.evaluate()
            }
        }
    }
}