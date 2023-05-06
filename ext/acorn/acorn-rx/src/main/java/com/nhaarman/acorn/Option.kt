package com.nhaarman.acorn

fun <T> T?.toOption(): Option<T> = this?.let { Some(it) } ?: None

fun <T> Option<T>.orNull(): T? = when (this) {
    is Some -> value
    is None -> null
}

sealed class Option<out T> {

    companion object {

        fun <T> empty(): Option<T> = None

        fun <T> just(value: T): Option<T> = Some(value)
    }
}

object None : Option<Nothing>()
data class Some<out T>(val value: T) : Option<T>()
