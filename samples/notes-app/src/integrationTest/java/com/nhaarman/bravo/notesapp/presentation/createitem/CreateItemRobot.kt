package com.nhaarman.bravo.notesapp.presentation.createitem

import com.nhaarman.bravo.testing.TestContext

fun TestContext.createItem(f: CreateItemRobot.() -> Unit) {
    CreateItemRobot(this).f()
}

class CreateItemRobot(context: TestContext) {

    val container = context.container<TestCreateItemContainer>()

    fun enterText(text: String) = container.textChanges.onNext(text)
    fun create() = container.createClicks.onNext(Unit)
}
