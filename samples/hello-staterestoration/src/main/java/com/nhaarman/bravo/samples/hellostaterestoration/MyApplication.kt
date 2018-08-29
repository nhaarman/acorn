package com.nhaarman.bravo.samples.hellostaterestoration

import android.app.Application
import com.nhaarman.bravo.android.AndroidLogger

class MyApplication : Application() {

    override fun onCreate() {
        bravo.logger = AndroidLogger()
        super.onCreate()
    }
}