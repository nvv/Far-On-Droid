package com.openfarmanager.android.di

import android.content.Context
import com.openfarmanager.android.App
import com.openfarmanager.android.core.command.executor.CommandExecutor
import com.openfarmanager.android.core.filesystem.FileSystemScanner
import com.openfarmanager.android.core.filesystem.StorageUtils
import com.openfarmanager.android.theme.ThemePref
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule {

    @Provides
    @Singleton
    fun provideAppContext(app: App): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideSharedPrefs(): ThemePref = App.themePref

    @Provides
    @Singleton
    fun provideStorageUtils(): StorageUtils = StorageUtils()

    @Provides
    @Singleton
    fun provideFileSystemScanner(pref: ThemePref, utils: StorageUtils): FileSystemScanner = FileSystemScanner(pref, utils)

    @Provides
    @Singleton
    fun provideCommandExecutor(): CommandExecutor = CommandExecutor()

}