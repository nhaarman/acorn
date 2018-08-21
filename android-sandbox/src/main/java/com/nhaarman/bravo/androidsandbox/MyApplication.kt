package com.nhaarman.bravo.androidsandbox

import android.app.Application
import com.nhaarman.bravo.android.TimberLogger
import com.nhaarman.bravo.logger

class MyApplication : Application() {

    override fun onCreate() {
        logger = TimberLogger()
        super.onCreate()
    }
}