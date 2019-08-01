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

package com.nhaarman.acorn.presentation

import kotlin.reflect.KClass

/**
 * A class representing the key for a Scene.
 */
class SceneKey(val value: String) {

    override fun toString(): String {
        return "SceneKey(value=$value)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SceneKey

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    companion object {

        /**
         * Create a [SceneKey] for given [Scene] class, consisting of its fully
         * qualified name.
         */
        fun <T : Scene<*>> from(sceneClass: KClass<T>): SceneKey {
            return SceneKey.from(sceneClass.java)
        }

        /**
         * Create a [SceneKey] for given [Scene] class, consisting of its fully
         * qualified name.
         */
        fun <T : Scene<*>> from(sceneClass: Class<T>): SceneKey {
            return SceneKey(sceneClass.name)
        }

        /**
         * Returns the default [SceneKey] for [T], consisting of its fully
         * qualified class name.
         */
        inline fun <reified T : Scene<*>> T.defaultKey(): SceneKey {
            return SceneKey.from(T::class)
        }

        inline fun <reified T : Scene<*>> defaultKey(): SceneKey {
            return from(T::class)
        }
    }
}
