package com.openfarmanager.android.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openfarmanager.android.filesystempanel.vm.FileSystemViewVM
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(FileSystemViewVM::class)
    protected abstract fun resumeViewModel(resumeViewModel: FileSystemViewVM): ViewModel
}