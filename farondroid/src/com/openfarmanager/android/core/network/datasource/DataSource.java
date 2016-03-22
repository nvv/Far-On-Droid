package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;

import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;

/**
 * @author Vlad Namashko
 */
public abstract class DataSource {

    protected Handler mHandler;
    protected NetworkPanel.DirectoryScanInfo mDirectoryScanInfo = new NetworkPanel.DirectoryScanInfo();

    public abstract String getNetworkType();

    public abstract NetworkEnum getNetworkTypeEnum();

    public abstract NetworkPanel.DirectoryScanInfo openDirectory(FileProxy directory) throws RuntimeException;

    public abstract void onUnlinkedAccount();

    public abstract void exitFromNetwork();

    public abstract boolean isSearchSupported();

    public abstract boolean isChangeEncodingSupported();

    public abstract void open(FileProxy file);

    public abstract FileProxy createFakeDirectory(String path);
}
