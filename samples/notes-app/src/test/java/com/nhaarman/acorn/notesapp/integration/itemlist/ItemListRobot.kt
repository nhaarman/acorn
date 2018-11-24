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
