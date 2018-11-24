/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.acorn.samples.hellostartactivity

import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.nhaarman.expect.expect
import org.junit.After
import org.junit.Test

class AppTest {

    private val appPackage = "com.nhaarman.acorn.samples.hellostartactivity"

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

    @Test
    // Rotating results in a new Activity
    fun clickThroughAppWithRotation() {
        setPortrait()

        startApp()
        clickMapsButton()

        expect(device.currentPackageName).toBe("com.google.android.apps.maps")

        rotate()

        expect(device.currentPackageName).toBe("com.google.android.apps.maps")

        exitMaps()

        expect(device.currentPackageName).toBe(appPackage)
    }

    @Test
    // Force stopping the app simulates process death.
    fun clickThroughAppWithStoppedApp() {
        setPortrait()

        startApp()
        clickMapsButton()

        expect(device.currentPackageName).toBe("com.google.android.apps.maps")

        Runtime.getRuntime().exec(arrayOf("am", "force-stop", appPackage))

        expect(device.currentPackageName).toBe("com.google.android.apps.maps")

        exitMaps()

        expect(device.currentPackageName).toBe(appPackage)
    }

    @After
    fun tearDown() {
        setPortrait()

        // Ensure any saved state is gone
        startApp()
        device.pressBack()
        device.pressBack()
        device.pressBack()
    }

    private fun startApp() {
        device.pressHome()

        val context = InstrumentationRegistry.getInstrumentation().targetContext
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
            UiSelector().text("START MAPS").className("android.widget.Button")
        )

        mapsButton.click()

        device.wait(
            Until.hasObject(By.pkg("com.google.android.apps.maps")),
            3000
        )
    }

    private fun exitMaps() {
        device.pressBack()
        val isAppPackage = device.wait(
            Until.hasObject(By.pkg(appPackage)),
            3000
        )

        if (isAppPackage != true) {
            device.pressBack()
            device.wait(
                Until.hasObject(By.pkg(appPackage)),
                3000
            )
        }
    }

    private fun setPortrait() {
        device.setOrientationNatural()
        Thread.sleep(100)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    private fun rotate() {
        if (device.isNaturalOrientation) {
            device.setOrientationRight()
        } else {
            device.setOrientationNatural()
        }

        Thread.sleep(100)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }
}