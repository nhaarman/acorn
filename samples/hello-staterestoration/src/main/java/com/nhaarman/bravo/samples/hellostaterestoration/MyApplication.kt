package com.nhaarman.bravo.samples.hellostaterestoration

import android.app.Application
import com.nhaarman.bravo.android.TimberLogger
import timber.log.Timber

class MyApplication : Application() {

    override fun onCreate() {
        Timber.plant(Timber.DebugTree())
        bravo.logger = TimberLogger()
        super.onCreate()
    }
}