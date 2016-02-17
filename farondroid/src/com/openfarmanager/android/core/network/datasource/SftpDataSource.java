package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;

/**
 * @author Vlad Namashko
 */
public class SftpDataSource implements DataSource {

    public Handler mHandler;

    private NetworkPanel.DirectoryScanInfo mDirectoryScanInfo = new NetworkPanel.DirectoryScanInfo();

    public SftpDataSource(Handler handler) {
        mHandler = handler;
    }

    @Override
    public String getNetworkType() {
        return "SFTP";
    }

    @Override
    public NetworkEnum getNetworkTypeEnum() {
        return NetworkEnum.SFTP;
    }

    @Override
    public NetworkPanel.DirectoryScanInfo openDirectory(FileProxy directory) throws RuntimeException {
        System.out.println("::::  Open Direcotry : " + directory.getFullPath() + "  " + directory.getParentPath());
        return mDirectoryScanInfo.set(App.sInstance.getSftpApi().getDirectoryFiles(directory.getFullPath()), getParentPath(directory.getParentPath()));
    }

    @Override
    public void onUnlinkedAccount() {

    }

    @Override
    public String getPath(String path) {
        return path;
    }

    @Override
    public String getParentPath(String path) {
        return path;
    }

    @Override
    public void exitFromNetwork() {
        App.sInstance.getSftpApi().closeChannel();
    }

    @Override
    public boolean isSearchSupported() {
        return false;
    }

    @Override
    public boolean isChangeEncodingSupported() {
        return true;
    }

    @Override
    public void open(FileProxy file) {

    }
}
