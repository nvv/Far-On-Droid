package com.openfarmanager.android.filesystem.actions.multi.network;

import android.content.Context;

import com.openfarmanager.android.App;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.core.network.datasource.DataSource;
import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.filesystem.actions.multi.MultiActionTask;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;

import java.io.File;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public abstract class NetworkActionMultiTask extends MultiActionTask {

    protected NetworkEnum mNetworkType;
    protected DataSource mDataSource;

    public NetworkActionMultiTask(BaseFileSystemPanel panel, List<File> items) {
        NetworkPanel networkPanel = (NetworkPanel) panel;
        mNetworkType = networkPanel.getNetworkType();
        mDataSource = networkPanel.getDataSource();
        initAction(panel.getContext(), panel.getPanelLocation(), items);
    }

    protected NetworkActionMultiTask() {
    }

    protected NetworkApi getApi() {
        return App.sInstance.getNetworkApi(mNetworkType);
    }
}
