package com.nhaarman.bravo.android

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.nhaarman.bravo.presentation.Container

class LinearLayoutContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), Container
