package com.openfarmanager.android.di

import com.openfarmanager.android.Main
import com.openfarmanager.android.filesystempanel.Panel
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): Main

    @ContributesAndroidInjector
    abstract fun contributePanelFragment(): Panel

}