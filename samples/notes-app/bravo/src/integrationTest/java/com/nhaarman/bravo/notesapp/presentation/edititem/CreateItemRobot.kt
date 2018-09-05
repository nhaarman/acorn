package com.nhaarman.bravo.notesapp.presentation.edititem

import com.nhaarman.bravo.testing.TestContext

fun TestContext.editItem(f: EditItemRobot.() -> Unit) {
    EditItemRobot(this).f()
}

class EditItemRobot(context: TestContext) {

    val container = context.container<TestEditItemContainer>()

    fun enterText(text: String) = container.enterText(text)

    fun save() = container.clickSave()
}
