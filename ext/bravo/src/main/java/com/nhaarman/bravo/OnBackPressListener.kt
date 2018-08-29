package com.nhaarman.bravo

import com.nhaarman.bravo.navigation.Navigator

/**
 * An interface [Navigator]s can implement to indicate they are interested in back
 * button presses.
 */
interface OnBackPressListener {

    /**
     * Invoked when the user presses the back button.
     *
     * @return `true` if the back press is handled by the implementation,
     *         `false` otherwise.
     */
    fun onBackPressed(): Boolean
}