package com.nhaarman.bravo.notesapp.presentation.itemlist

import com.nhaarman.bravo.notesapp.presentation.createitem.CreateItemRobot
import com.nhaarman.bravo.testing.TestContext
import com.nhaarman.expect.expect

fun TestContext.itemList(f: ItemListRobot.() -> Unit) {
    ItemListRobot(this).f()
}

class ItemListRobot(private val context: TestContext) {

    val container = context.container<TestItemListContainer>()

    val items get() = container.items

    fun verifyVisible(items: List<String>) {
        expect(container.items.map { it.text }).toBe(items)
    }

    fun verifyVisible(vararg item: String) {
        verifyVisible(item.toList())
    }

    fun clickItem(position: Int) {
        container.clickItem(position)
    }

    fun requestCreateItem(): CreateItemRobot {
        container.createClicks.onNext(Unit)

        return CreateItemRobot(context)
    }

    fun delete(position: Int) {
        container.deleteClicks.onNext(items[position])
    }

    fun pressBack() {
        context.pressBack()
    }
}
