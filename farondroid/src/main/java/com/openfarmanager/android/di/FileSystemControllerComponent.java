package com.openfarmanager.android.di;

import com.openfarmanager.android.view.presenters.ActionBarPresenterImpl;
import com.openfarmanager.android.view.presenters.NetworkActionBarPresenterImpl;
import com.openfarmanager.android.view.presenters.QuickActionViewPresenterImpl;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Vlad Namashko
 */
@Singleton
@Component(modules = {FileSystemControllerModule.class})
public interface FileSystemControllerComponent {

    void inject(ActionBarPresenterImpl presenter);

    void inject(NetworkActionBarPresenterImpl presenter);

    void inject(QuickActionViewPresenterImpl presenter);
}
