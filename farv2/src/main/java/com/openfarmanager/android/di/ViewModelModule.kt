package com.openfarmanager.android.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openfarmanager.android.filesystempanel.vm.BottomBarVM
import com.openfarmanager.android.filesystempanel.vm.CopyDialogVM
import com.openfarmanager.android.filesystempanel.vm.FileSystemPanelVM
import com.openfarmanager.android.filesystempanel.vm.MainViewVM
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(FileSystemPanelVM::class)
    protected abstract fun fileSystemPanelViewModel(fileSystemPanelVM: FileSystemPanelVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewVM::class)
    protected abstract fun mainViewModel(mainVM: MainViewVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CopyDialogVM::class)
    protected abstract fun copyDialogVM(copyDialogVM: CopyDialogVM): ViewModel

}