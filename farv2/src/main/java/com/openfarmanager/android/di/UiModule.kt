package com.openfarmanager.android.di

import com.openfarmanager.android.Main
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class UiModule {

    abstract fun contributeMainActivity(): Main

}