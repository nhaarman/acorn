/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.samples.hellostartactivity

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.uiautomator.By
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector
import android.support.test.uiautomator.Until
import com.nhaarman.expect.expect
import org.junit.Test

class AppTest {

    private val appPackage = "com.nhaarman.bravo.samples.hellostartactivity"

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Test
    fun clickThroughApp() {
        startApp()
        clickMapsButton()

        expect(device.currentPackageName).toBe("com.google.android.apps.maps")

        exitMaps()

        expect(device.currentPackageName).toBe(appPackage)

        clickMapsButton()

        expect(device.currentPackageName).toBe("com.google.android.apps.maps")

        exitMaps()

        expect(device.currentPackageName).toBe(appPackage)
    }

    private fun startApp() {
        device.pressHome()

        val context = InstrumentationRegistry.getContext()
        val intent = context.packageManager
            .getLaunchIntentForPackage(appPackage)
            ?.also {
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }

        context.startActivity(intent)

        device.wait(
            Until.hasObject(By.pkg(appPackage)),
            3000
        )
    }

    private fun clickMapsButton() {
        val mapsButton = device.findObject(
            UiSelector().text("Start Maps").className("android.widget.Button")
        )

        mapsButton.click()

        device.wait(
            Until.hasObject(By.pkg("com.nhaarman.bravo.samples.hellostartactivity")),
            3000
        )
    }

    private fun exitMaps() {
        device.pressBack()
        device.pressBack()
        device.wait(
            Until.hasObject(By.pkg(appPackage)),
            3000
        )
    }
}