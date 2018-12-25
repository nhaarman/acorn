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

package com.nhaarman.acorn.notesapp.integration

import com.nhaarman.acorn.notesapp.ImmediateMainThreadExtension
import com.nhaarman.acorn.notesapp.NoRxErrorsExtension
import com.nhaarman.acorn.notesapp.integration.presentation.createitem.createItem
import com.nhaarman.acorn.notesapp.integration.presentation.edititem.editItem
import com.nhaarman.acorn.notesapp.integration.presentation.itemlist.itemList
import com.nhaarman.acorn.notesapp.navigation.NotesAppNavigator
import com.nhaarman.acorn.testing.TestContext
import com.nhaarman.acorn.testing.TestContext.Companion.testWith
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(NoRxErrorsExtension::class, ImmediateMainThreadExtension::class)
class NotesAppTest {

    val noteAppComponent = TestNotesAppComponent()
    val navigator = NotesAppNavigator(noteAppComponent, null)
    val context = TestContext.create(navigator, NotesAppTestContainerProvider)

    @Test
    fun `creating an item`() = testWith(context) {
        itemList {
            verifyVisible(emptyList())
            requestCreateItem()
        }

        createItem {
            enterText("Foo")
            create()
        }

        itemList {
            verifyVisible("Foo")
        }
    }

    @Test
    fun `editing an item`() = testWith(context) {
        itemList {
            verifyVisible(emptyList())
            requestCreateItem()
        }

        createItem {
            enterText("Foo")
            create()
        }

        itemList {
            verifyVisible("Foo")
            clickItem(0)
        }

        editItem {
            enterText("Bar")
            save()
        }

        itemList {
            verifyVisible("Bar")
            clickItem(0)
        }
    }

    @Test
    fun `deleting an item`() = testWith(context) {
        itemList {
            verifyVisible(emptyList())
            requestCreateItem()
        }

        createItem {
            enterText("Foo")
            create()
        }

        itemList {
            verifyVisible("Foo")
            delete(0)
            verifyVisible()
        }
    }

    @Test
    fun `creating multiple items`() = testWith(context) {
        itemList {
            verifyVisible(emptyList())
            requestCreateItem()
        }

        createItem {
            enterText("Foo")
            create()
        }

        itemList {
            requestCreateItem()
        }

        createItem {
            enterText("Bar")
            create()
        }

        itemList {
            verifyVisible("Foo", "Bar")
        }
    }
}
