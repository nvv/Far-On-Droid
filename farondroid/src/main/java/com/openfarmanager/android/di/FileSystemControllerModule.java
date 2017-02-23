package com.openfarmanager.android.di;

import android.os.Handler;

import com.openfarmanager.android.controllers.FileSystemController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Vlad Namashko
 */
@Module
@Singleton
public class FileSystemControllerModule {

    private FileSystemController mFileSystemController;

    public FileSystemControllerModule(FileSystemController controller) {
        mFileSystemController = controller;
    }

    @Provides
    Handler providePanelHandler() {
        return mFileSystemController.getPanelHandler();
    }
}
