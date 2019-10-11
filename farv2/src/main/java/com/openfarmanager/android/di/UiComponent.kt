package com.openfarmanager.android.di

import com.openfarmanager.android.App
import com.openfarmanager.android.filesystempanel.adapter.FileSystemAdapter
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(
        modules = [
            AppModule::class
        ]
)
@Singleton
interface UiComponent {

    fun inject(adapter: FileSystemAdapter)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: App): Builder

        fun build(): UiComponent
    }

}