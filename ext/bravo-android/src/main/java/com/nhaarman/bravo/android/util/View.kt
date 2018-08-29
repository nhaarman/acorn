package com.nhaarman.bravo.android.util

import android.os.Parcelable
import android.util.SparseArray
import android.view.View

fun View.saveHierarchyState() = SparseArray<Parcelable>().apply { saveHierarchyState(this) }
