package com.openfarmanager.android

import android.app.Activity
import android.app.Application
import com.openfarmanager.android.di.DaggerAppComponent
import com.openfarmanager.android.di.DaggerUiComponent
import com.openfarmanager.android.di.UiComponent
import com.openfarmanager.android.theme.ThemePref
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class App : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)

        uiComponent = DaggerUiComponent.builder()
                .application(this)
                .build()

        themePref.init(this)
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity>? {
        return dispatchingAndroidInjector
    }

    companion object {
        val themePref = ThemePref()

        lateinit var uiComponent: UiComponent
    }
}