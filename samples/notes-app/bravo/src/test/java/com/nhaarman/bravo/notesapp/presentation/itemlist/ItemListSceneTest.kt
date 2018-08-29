package com.nhaarman.bravo.notesapp.presentation.itemlist

import com.nhaarman.bravo.notesapp.ImmediateMainThreadExtension
import com.nhaarman.bravo.notesapp.NoRxErrorsExtension
import com.nhaarman.bravo.notesapp.note.MemoryNoteItemsRepository
import com.nhaarman.bravo.notesapp.note.NoteItem
import com.nhaarman.bravo.state.ContainerState
import com.nhaarman.bravo.state.containerState
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.subjects.PublishSubject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(NoRxErrorsExtension::class, ImmediateMainThreadExtension::class)
class ItemListSceneTest {

    val noteItemsRepository = MemoryNoteItemsRepository()

    val item1 = noteItemsRepository.create("1").blockingGet()
    val item2 = noteItemsRepository.create("2").blockingGet()

    val listener = mock<ItemListScene.Events>()

    val scene = ItemListScene(
        noteItemsRepository,
        listener,
        null
    )

    private val container = TestItemListContainer()

    @Test
    fun `on attach sets items`() {
        /* Given */
        scene.onStart()

        /* When */
        scene.attach(container)

        /* Then */
        expect(container.items).toBe(listOf(item1, item2))
    }

    @Test
    fun `on item list change updates items to container`() {
        /* Given */
        scene.onStart()
        scene.attach(container)

        /* When */
        val item3 = noteItemsRepository.create("3").blockingGet()

        /* Then */
        expect(container.items).toBe(listOf(item1, item2, item3))
    }

    @Test
    fun `on create item clicked`() {
        /* Given */
        scene.onStart()
        scene.attach(container)

        /* When */
        container.createClicks.onNext(Unit)

        /* Then */
        verify(listener).createItemRequested()
    }

    @Test
    fun `on show item clicked`() {
        /* Given */
        scene.onStart()
        scene.attach(container)

        /* When */
        container.itemClicks.onNext(item2)

        /* Then */
        verify(listener).showItemRequested(item2)
    }

    @Test
    fun `restoring state`() {
        /* Given */
        scene.attach(container)
        container.state = 3

        val state = scene.saveInstanceState()

        val container2 = TestItemListContainer()

        /* When */
        val scene2 = ItemListScene(noteItemsRepository, listener, state)
        scene2.attach(container2)

        /* Then */
        expect(container.state).toBe(3)
    }
}

private open class TestItemListContainer : ItemListContainer {

    override var items: List<NoteItem> = emptyList()

    override val createClicks = PublishSubject.create<Unit>()
    override val itemClicks = PublishSubject.create<NoteItem>()
    override val deleteClicks = PublishSubject.create<NoteItem>()

    var state = 0

    override fun saveInstanceState(): ContainerState {
        return containerState { it["state"] = 3 }
    }

    override fun restoreInstanceState(bundle: ContainerState) {
        state = bundle["state"] ?: 0
    }
}