package com.nhaarman.bravo

/**
 * A wrapper around a [Map] that is used for state saving and restoration.
 */
class BravoBundle private constructor(
    private val map: MutableMap<String, Any?> = mutableMapOf()
) {

    operator fun set(key: String, value: Number?) {
        map[key] = value
    }

    operator fun set(key: String, value: String?) {
        map[key] = value
    }

    operator fun set(key: String, value: BravoBundle?) {
        map[key] = value
    }

    @Deprecated("Nope")
    operator fun set(key: String, value: Any?) {
        map[key] = value
    }

    val entries: Set<Map.Entry<String, Any?>>
        get() {
            return map.entries
        }

//    operator fun <T> set(key: String, t: T?) {
//        map[key] = t
//    }

    private fun get(key: String) = map[key]

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(key: String, default: T? = null) = get(key) as? T ?: default

    override fun toString(): String {
        return "BravoBundle($map)"
    }

    companion object {

        operator fun invoke() = BravoBundle()

        fun bundle(init: (BravoBundle) -> Unit) = BravoBundle().also(init)
    }
}
