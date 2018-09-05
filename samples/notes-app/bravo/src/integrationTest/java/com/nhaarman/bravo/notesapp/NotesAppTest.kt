package com.nhaarman.bravo.notesapp

import com.nhaarman.bravo.notesapp.navigation.NotesAppNavigator
import com.nhaarman.bravo.notesapp.presentation.createitem.createItem
import com.nhaarman.bravo.notesapp.presentation.edititem.editItem
import com.nhaarman.bravo.notesapp.presentation.itemlist.itemList
import com.nhaarman.bravo.testing.TestContext
import org.junit.jupiter.api.Test

class NotesAppTest {

    val noteAppComponent = TestNotesAppComponent()
    val navigator = NotesAppNavigator(noteAppComponent, null)
    val context = TestContext.create(navigator, NotesAppTestContainerProvider)

    @Test
    fun `creating an item`() = with(context) {
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
    fun `editing an item`() = with(context) {
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
    fun `deleting an item`() = with(context) {
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
    fun `creating multiple items`() = with(context) {
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
