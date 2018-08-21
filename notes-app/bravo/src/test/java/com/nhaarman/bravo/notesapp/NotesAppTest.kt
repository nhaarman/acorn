package com.nhaarman.bravo.notesapp

import com.nhaarman.bravo.BravoBundle
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.notesapp.note.MemoryNoteItemsRepository
import com.nhaarman.bravo.notesapp.note.NoteItem
import com.nhaarman.bravo.notesapp.note.NoteItemsRepository
import com.nhaarman.bravo.notesapp.navigation.NoteAppNavigator
import com.nhaarman.bravo.notesapp.presentation.createitem.CreateItemContainer
import com.nhaarman.bravo.notesapp.presentation.createitem.CreateItemScene
import com.nhaarman.bravo.notesapp.presentation.itemlist.ItemListContainer
import com.nhaarman.bravo.notesapp.presentation.itemlist.ItemListScene
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.expect.expect
import io.reactivex.subjects.PublishSubject
import org.junit.jupiter.api.Test

class NotesAppTest {

    val noteAppComponent = object : NotesAppComponent {
        override val noteItemsRepository: NoteItemsRepository = MemoryNoteItemsRepository()
    }

    val navigator = NoteAppNavigator(noteAppComponent, null)

    val context = NoteAppContext(navigator)

    @Test
    fun `creating an item`() {
        context.start {
            verifyVisible(emptyList())
            requestCreateItem()
        }

        context.createItem {
            enterText("Foo")
            create()
        }

        context.itemList {
            verifyVisible("Foo")
        }
    }

    @Test
    fun `creating multiple items`() {
        context.start {
            verifyVisible(emptyList())
            requestCreateItem()
        }

        context.createItem {
            enterText("Foo")
            create()
        }

        context.itemList {
            requestCreateItem()
        }

        context.createItem {
            enterText("Bar")
            create()
        }

        context.itemList {
            verifyVisible("Foo", "Bar")
        }
    }
}

@Suppress("UNCHECKED_CAST")
class NoteAppContext(val navigator: NoteAppNavigator) : Navigator.Events {

    var scene: Scene<out Container>? = null
    var container: Container? = null
    fun <T> container(): T = container as T

    override fun scene(scene: Scene<out Container>) {
        (this.scene as? Scene<Container>)?.detach(container!!)
        this.scene = scene
        when (scene) {
            is ItemListScene -> {
                container = TestItemListContainer()
                scene.attach(container as ItemListContainer)
            }
            is CreateItemScene -> {
                container = TestCreateItemContainer()
                scene.attach(container as CreateItemContainer)
            }
            else -> error("Unknown scene")
        }
    }

    override fun finished() {
        TODO("finished")
    }

    fun start(): ItemListRobot {
        navigator.addListener(this)
        navigator.onStart()

        return ItemListRobot(this)
    }

    inline fun start(f: ItemListRobot.() -> Unit) {
        navigator.addListener(this)
        navigator.onStart()

        ItemListRobot(this).f()
    }

    inline fun itemList(f: ItemListRobot.() -> Unit) {
        ItemListRobot(this).f()
    }

    fun createItem(f: CreateItemRobot.() -> Unit) {
        CreateItemRobot(this).f()
    }
}

class CreateItemRobot(context: NoteAppContext) {

    val container = context.container<TestCreateItemContainer>()

    fun enterText(text: String) = container.textChanges.onNext(text)
    fun create() = container.createClicks.onNext(Unit)
}

class TestCreateItemContainer : CreateItemContainer {

    override fun setInitialText(text: String?) {
    }

    override val textChanges = PublishSubject.create<String>()

    override val createClicks = PublishSubject.create<Unit>()

    override fun saveInstanceState(): BravoBundle {
        return BravoBundle()
    }

    override fun restoreInstanceState(bundle: BravoBundle) {
    }
}

class ItemListRobot(private val context: NoteAppContext) {

    val container = context.container<TestItemListContainer>()

    val items get() = container.items

    fun verifyVisible(items: List<String>) {
        expect(container.items.map { it.text }).toBe(items)
    }

    fun verifyVisible(vararg item: String) {
        verifyVisible(item.toList())
    }

    fun requestCreateItem(): CreateItemRobot {
        container.createClicks.onNext(Unit)

        return CreateItemRobot(context)
    }

    fun delete(position: Int) {
        container.deleteClicks.onNext(items[position])
    }

    fun pressBack() {
        context.navigator.onBackPressed()
    }
}

class TestItemListContainer : ItemListContainer {

    override var items: List<NoteItem> = emptyList()

    override val createClicks = PublishSubject.create<Unit>()

    override val itemClicks = PublishSubject.create<NoteItem>()

    override val deleteClicks = PublishSubject.create<NoteItem>()

    override fun saveInstanceState(): BravoBundle {
        return BravoBundle()
    }

    override fun restoreInstanceState(bundle: BravoBundle) {
    }
}
