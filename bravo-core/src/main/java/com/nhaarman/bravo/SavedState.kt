package com.nhaarman.bravo

/**
 * Used to save and restore instance states.
 *
 * This interface is implemented by:
 *
 *  - [ContainerState]
 *  - [SceneState]
 *  - [NavigatorState]
 *
 * Each of these implementations provide the same behavior, but allow for
 * extra type safety when using them.
 */
interface SavedState {

    val entries: Set<Map.Entry<String, Any?>>

    operator fun set(key: String, value: Number?)
    operator fun set(key: String, value: String?)

    fun setUnchecked(key: String, value: Any?)

    operator fun <T : Any> get(key: String, default: T? = null): T?
}

private data class BaseSavedState(
    private val map: MutableMap<String, Any?> = mutableMapOf()
) : SavedState {

    override val entries: Set<Map.Entry<String, Any?>>
        get() {
            return map.entries
        }

    override fun set(key: String, value: Number?) {
        map[key] = value
    }

    override fun set(key: String, value: String?) {
        map[key] = value
    }

    override fun setUnchecked(key: String, value: Any?) {
        map[key] = value
    }

    private fun get(key: String) = map[key]

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(key: String, default: T?): T? {
        return get(key) as? T ?: default
    }

    override fun toString(): String {
        return "$map"
    }
}

class ContainerState private constructor(
    private val delegate: BaseSavedState
) : SavedState by delegate {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContainerState

        if (delegate != other.delegate) return false

        return true
    }

    override fun hashCode(): Int {
        return delegate.hashCode()
    }

    override fun toString(): String {
        return "ContainerState(delegate=$delegate)"
    }

    companion object {

        operator fun invoke() = ContainerState(BaseSavedState())

        fun containerState(init: (ContainerState) -> Unit) = ContainerState().also(init)
    }
}

class SceneState private constructor(
    private val delegate: BaseSavedState
) : SavedState by delegate {

    operator fun set(key: String, value: ContainerState?) {
        setUnchecked(key, value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SceneState

        if (delegate != other.delegate) return false

        return true
    }

    override fun hashCode(): Int {
        return delegate.hashCode()
    }

    override fun toString(): String {
        return "SceneState(delegate=$delegate)"
    }

    companion object {

        operator fun invoke() = SceneState(BaseSavedState())

        fun sceneState(init: (SceneState) -> Unit) = SceneState().also(init)
    }
}

class NavigatorState private constructor(
    private val delegate: BaseSavedState
) : SavedState by delegate {

    operator fun set(key: String, value: SceneState?) {
        setUnchecked(key, value)
    }

    operator fun set(key: String, value: NavigatorState?) {
        setUnchecked(key, value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NavigatorState

        if (delegate != other.delegate) return false

        return true
    }

    override fun hashCode(): Int {
        return delegate.hashCode()
    }

    override fun toString(): String {
        return "NavigatorState(delegate=$delegate)"
    }

    companion object {

        operator fun invoke() = NavigatorState(BaseSavedState())

        fun navigatorState(init: (NavigatorState) -> Unit) = NavigatorState().also(init)
    }
}