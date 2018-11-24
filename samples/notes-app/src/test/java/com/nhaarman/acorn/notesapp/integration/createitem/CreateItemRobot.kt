/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.notesapp.integration.presentation.createitem

import com.nhaarman.bravo.testing.TestContext

fun TestContext.createItem(f: CreateItemRobot.() -> Unit) {
    CreateItemRobot(this).f()
}

class CreateItemRobot(context: TestContext) {

    val container = context.container<TestCreateItemContainer>()

    fun enterText(text: String) = container.textChanges.onNext(text)
    fun create() = container.createClicks.onNext(Unit)
}
