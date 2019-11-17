package com.openfarmanager.android.di

import com.openfarmanager.android.Main
import com.openfarmanager.android.filesystempanel.fragments.dialogs.FileActionFragment
import com.openfarmanager.android.filesystempanel.fragments.PanelFragment
import com.openfarmanager.android.filesystempanel.fragments.dialogs.CommandFragment
import com.openfarmanager.android.filesystempanel.fragments.dialogs.CopyDialogFragment
import com.openfarmanager.android.filesystempanel.fragments.dialogs.ProgressFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): Main

    @ContributesAndroidInjector
    abstract fun contributePanelFragment(): PanelFragment

    @ContributesAndroidInjector
    abstract fun contributeFileActionFragment(): FileActionFragment

    @ContributesAndroidInjector
    abstract fun contributeCopyDialogFragment(): CopyDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCommandFragment(): CommandFragment
}