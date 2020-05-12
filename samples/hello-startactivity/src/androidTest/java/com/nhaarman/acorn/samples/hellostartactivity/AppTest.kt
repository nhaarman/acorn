/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nhaarman.acorn.samples.hellostartactivity

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
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
        clickSettingsButton()

        expect(device.currentPackageName).toBe("com.android.settings")

        exitSettings()

        expect(device.currentPackageName).toBe(appPackage)

        clickSettingsButton()

        expect(device.currentPackageName).toBe("com.android.settings")

        exitSettings()

        expect(device.currentPackageName).toBe(appPackage)
        onView(withText("OPEN SETTINGS")).check(matches(isDisplayed()))
    }

    @Test
    // Rotating results in a new Activity
    fun clickThroughAppWithRotation() {
        setPortrait()

        startApp()
        clickSettingsButton()

        expect(device.currentPackageName).toBe("com.android.settings")

        rotate()

        expect(device.currentPackageName).toBe("com.android.settings")

        exitSettings()

        expect(device.currentPackageName).toBe(appPackage)
        onView(withText("OPEN SETTINGS")).check(matches(isDisplayed()))
    }

    @Test
    // Force stopping the app simulates process death.
    fun clickThroughAppWithStoppedApp() {
        setPortrait()

        startApp()
        clickSettingsButton()

        expect(device.currentPackageName).toBe("com.android.settings")

        Runtime.getRuntime().exec(arrayOf("am", "force-stop", appPackage))

        expect(device.currentPackageName).toBe("com.android.settings")

        exitSettings()

        expect(device.currentPackageName).toBe(appPackage)
        onView(withText("OPEN SETTINGS")).check(matches(isDisplayed()))
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

    private fun clickSettingsButton() {
        val settingsButton = device.findObject(
            UiSelector().text("OPEN SETTINGS").className("android.widget.Button")
        )

        settingsButton.click()

        device.wait(
            Until.hasObject(By.pkg("com.android.settings")),
            3000
        )
    }

    private fun exitSettings() {
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
