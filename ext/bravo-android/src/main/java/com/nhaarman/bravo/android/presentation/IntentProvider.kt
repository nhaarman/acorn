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

package com.nhaarman.bravo.android.presentation

import android.app.Activity

import android.content.Intent
import com.nhaarman.bravo.navigation.TransitionData
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import kotlin.reflect.KClass

/**
 * Provides [Intent] instances for starting external Activities.
 */
interface IntentProvider {

    /**
     * Creates an [Intent] that can be used to start an [Activity] for given [Scene].
     *
     * @param scene The Scene instance to create an Intent for.
     * @param data Any transition data.
     *
     * @return The created Intent, or `null` if this IntentProvider cannot create
     * an Intent for given Scene.
     */
    fun intentFor(scene: Scene<out Container>, data: TransitionData?): Intent?

    /**
     * Called when an [Activity] returned with a result.
     *
     * It is possible that this method is called when [intentFor] returned `null`
     * for given [scene]. If this is the case, return `false` and discard the result.
     *
     * @param scene The scene for which the result came back.
     * @param resultCode The result code of the Activity that returned.
     * @param data The data [Intent] the Activity returned.
     *
     * @return true to consume the result, meaning other IntentProvider instances
     * will not receive this call.
     */
    fun onActivityResult(scene: Scene<out Container>, resultCode: Int, data: Intent?): Boolean
}

/**
 * An [IntentProvider] that delegates to other implementations.
 */
class ComposingIntentProvider private constructor(
    private val delegates: Sequence<IntentProvider>
) : IntentProvider {

    override fun intentFor(scene: Scene<out Container>, data: TransitionData?): Intent? {
        return delegates
            .mapNotNull { it.intentFor(scene, data) }
            .firstOrNull()
    }

    override fun onActivityResult(scene: Scene<out Container>, resultCode: Int, data: Intent?): Boolean {
        return delegates
            .map { it.onActivityResult(scene, resultCode, data) }
            .filter { it == true }
            .firstOrNull() ?: false
    }

    companion object {

        fun from(intentProviders: List<IntentProvider>): ComposingIntentProvider {
            return ComposingIntentProvider(intentProviders.asSequence())
        }

        fun from(vararg intentProviders: IntentProvider): ComposingIntentProvider {
            return ComposingIntentProvider(intentProviders.asSequence())
        }
    }
}

/**
 * An [IntentProvider] that facilitates intent providing for [ExternalScene]
 * instances.
 */
abstract class ExternalSceneIntentProvider<T : ExternalScene>(
    private val sceneClass: Class<T>
) : IntentProvider {

    constructor(sceneClass: KClass<T>) : this(sceneClass.java)

    override fun intentFor(scene: Scene<out Container>, data: TransitionData?): Intent? {
        if (scene.javaClass != sceneClass) return null

        @Suppress("UNCHECKED_CAST")
        return intentFor(scene as T, data)
    }

    abstract fun intentFor(scene: T, data: TransitionData?): Intent?

    override fun onActivityResult(scene: Scene<out Container>, resultCode: Int, data: Intent?): Boolean {
        if (scene.javaClass != sceneClass) return false

        (scene as ExternalScene).finished()

        return true
    }
}

/**
 * An [IntentProvider] that provides no [Intent]s ever.
 */
object NoIntentProvider : IntentProvider {

    override fun intentFor(scene: Scene<out Container>, data: TransitionData?): Intent? {
        return null
    }

    override fun onActivityResult(scene: Scene<out Container>, resultCode: Int, data: Intent?): Boolean {
        return false
    }
}
