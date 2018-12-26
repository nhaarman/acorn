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

package com.nhaarman.acorn.notesapp.integration.presentation.itemlist

import com.nhaarman.acorn.notesapp.integration.presentation.createitem.CreateItemRobot
import com.nhaarman.acorn.testing.TestContext
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
