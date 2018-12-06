package com.nhaarman.acorn.presentation

import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import org.junit.jupiter.api.Test

/**
 * A set of tests to specify default Scene behavior.
 */
class SceneTest {

    val scene = spy(TestScene())

    @Test
    fun `default scene key`() {
        /* Given */
        val scene = TestScene()

        /* Then */
        expect(scene.key).toBe(SceneKey.from(TestScene::class))
    }

    @Test
    fun `calling onStart does nothing`() {
        /* When */
        scene.onStart()

        /* Then */
        scene.inOrder {
            verify().onStart()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `calling attach does nothing`() {
        /* When */
        scene.attach(mock())

        /* Then */
        scene.inOrder {
            verify().attach(any())
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `calling detach does nothing`() {
        /* When */
        scene.detach(mock())

        /* Then */
        scene.inOrder {
            verify().detach(any())
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `calling onStop does nothing`() {
        /* When */
        scene.onStop()

        /* Then */
        scene.inOrder {
            verify().onStop()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `calling onDestroy does nothing`() {
        /* When */
        scene.onDestroy()

        /* Then */
        scene.inOrder {
            verify().onDestroy()
            verifyNoMoreInteractions()
        }
    }

    open class TestScene : Scene<Container>
}