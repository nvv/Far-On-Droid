package com.openfarmanager.android.filesystem.actions.network;

import android.support.v4.app.FragmentManager;
import com.openfarmanager.android.App;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.actions.FileActionTask;
import com.openfarmanager.android.model.NetworkEnum;

import java.io.File;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public abstract class NetworkActionTask extends FileActionTask {

    protected NetworkEnum mNetworkType;

    protected NetworkActionTask(FragmentManager fragmentManager, OnActionListener listener, List<File> items) {
        super(fragmentManager, listener, items);
    }

    protected NetworkActionTask() {
    }

    protected NetworkApi getApi() {
        return App.sInstance.getNetworkApi(mNetworkType);
    }
}
