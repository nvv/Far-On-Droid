package com.openfarmanager.android.filesystem.actions.network;

import android.support.v4.app.FragmentManager;
import com.openfarmanager.android.App;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.core.network.datasource.DataSource;
import com.openfarmanager.android.filesystem.actions.FileActionTask;
import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;

import java.io.File;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public abstract class NetworkActionTask extends FileActionTask {

    protected NetworkEnum mNetworkType;
    protected DataSource mDataSource;

    protected NetworkActionTask(FragmentManager fragmentManager, BaseFileSystemPanel panel, OnActionListener listener, List<File> items) {
        super(fragmentManager, listener, items);
        initNetworkPanelInfo(panel);
    }

    protected void initNetworkPanelInfo(BaseFileSystemPanel panel) {
        NetworkPanel networkPanel = (NetworkPanel) panel;
        mNetworkType = networkPanel.getNetworkType();
        mDataSource = networkPanel.getDataSource();
    }

    protected NetworkActionTask() {
    }

    protected NetworkApi getApi() {
        return App.sInstance.getNetworkApi(mNetworkType);
    }
}
