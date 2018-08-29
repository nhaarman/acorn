package com.nhaarman.bravo.samples.hellonavigation

import android.app.Application
import com.nhaarman.bravo.android.AndroidLogger

class MyApplication : Application() {

    override fun onCreate() {
        bravo.logger = AndroidLogger()
        super.onCreate()
    }
}