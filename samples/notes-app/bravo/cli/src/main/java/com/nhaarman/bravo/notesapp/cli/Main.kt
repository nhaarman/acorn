package com.nhaarman.bravo.notesapp.cli

import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.nhaarman.bravo.BravoBundle
import com.nhaarman.bravo.notesapp.cli.note.MemoryNoteItemsRepository
import com.nhaarman.bravo.notesapp.mainThread
import com.nhaarman.bravo.notesapp.note.NoteItem
import com.nhaarman.bravo.notesapp.note.NoteItemsRepository
import com.nhaarman.bravo.notesapp.presentation.NoteAppComponent
import com.nhaarman.bravo.notesapp.presentation.PrimaryNavigator
import com.nhaarman.bravo.notesapp.presentation.itemlist.ItemListContainer
import com.nhaarman.bravo.notesapp.presentation.itemlist.ItemListScene
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import io.reactivex.Observable
import io.reactivex.internal.schedulers.ImmediateThinScheduler
import java.lang.Thread.sleep
import java.util.Random
import java.util.concurrent.CountDownLatch

fun main(args: Array<String>) {
    mainThread = ImmediateThinScheduler.INSTANCE

    val latch = CountDownLatch(1)
    val listener = object : PrimaryNavigator.Events {

        override fun createItemRequested() {
            error("")
        }

        override fun scene(scene: Scene<out Container>) {
            when (scene) {
                is ItemListScene -> scene.attach(object : ItemListContainer {

                    override var items: List<NoteItem> = emptyList()
                        set(value) {

                            val terminal = DefaultTerminalFactory().createTerminal()
                            val screen = TerminalScreen(terminal)
                            screen.startScreen()

                            val random = Random()
                            val terminalSize = screen.terminalSize
                            for (column in 0 until terminalSize.columns) {
                                for (row in 0 until terminalSize.rows) {
                                    screen.setCharacter(
                                        column, row, TextCharacter(
                                            ' ',
                                            TextColor.ANSI.DEFAULT,
                                            // This will pick a random background color
                                            TextColor.ANSI.values()[random.nextInt(TextColor.ANSI.values().size)]
                                        )
                                    )
                                }
                            }
                            screen.refresh()

                            sleep(2000)
                            return

                            terminal.enterPrivateMode()

//                            sleep(2000)
                            terminal.setCursorPosition(0, 6000)
                            sleep(2000)

                            terminal.newTextGraphics().apply {
                                value.forEachIndexed { index, noteItem ->
                                    putString(0, index, noteItem.text)
                                }

                                terminal.setCursorPosition(0, 6000)
                                terminal.flush()
                            }

                            terminal.bell()
                            terminal.flush()

                            sleep(2000)
                            terminal.close()
                        }

                    override val createClicks: Observable<Unit>
                        get() = Observable.never()

                    override val itemClicks: Observable<NoteItem>
                        get() = Observable.never()

                    override val deleteClicks: Observable<NoteItem>
                        get() = Observable.never()

                    override fun saveInstanceState(): BravoBundle {
                        return BravoBundle()
                    }

                    override fun restoreInstanceState(bundle: BravoBundle) {
                    }
                })
            }
        }

        override fun finished() {
            error("")
        }
    }

    val navigator = PrimaryNavigator(CLINoteAppComponent(), null)
    navigator.addListener(listener)
    navigator.onStart()

//    latch.await(5000, TimeUnit.MILLISECONDS)
}

class CLINoteAppComponent : NoteAppComponent {

    override val noteItemsRepository: NoteItemsRepository by lazy {
        MemoryNoteItemsRepository()
            .also {
                (0..100)
                    .forEach { id -> it.create("Note item #$id").subscribe() }
            }
    }
}
