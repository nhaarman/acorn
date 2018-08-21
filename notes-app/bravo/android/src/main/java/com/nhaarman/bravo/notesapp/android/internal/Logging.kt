package com.nhaarman.bravo.notesapp.android.internal

import com.nhaarman.bravo.logger

internal fun v(tag: String, message: Any?) = logger?.v(tag, message)
internal fun d(tag: String, message: Any?) = logger?.d(tag, message)
internal fun i(tag: String, message: Any?) = logger?.i(tag, message)
internal fun w(tag: String, message: Any?) = logger?.w(tag, message)
