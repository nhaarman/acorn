package com.nhaarman.circleci

import com.nhaarman.acorn.android.AcornAppCompatActivity
import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.android.presentation.ViewControllerFactory

class MainActivity : AcornAppCompatActivity() {

    override fun provideNavigatorProvider(): NavigatorProvider {
        return navigatorProvider
    }

    override fun provideViewControllerFactory(): ViewControllerFactory {
        return ViewFactoryProvider.viewControllerFactory
    }
}
