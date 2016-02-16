package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.NetworkEnum;

import java.util.List;

/**
 * @author Vlad Namashko
 */
public class SmbDataSource implements DataSource {

    public Handler mHandler;

    public SmbDataSource(Handler handler) {
        mHandler = handler;
    }

    @Override
    public String getNetworkType() {
        return "SMB";
    }

    @Override
    public NetworkEnum getNetworkTypeEnum() {
        return NetworkEnum.SMB;
    }

    @Override
    public List<FileProxy> openDirectory(FileProxy directory) throws RuntimeException {
        return App.sInstance.getSmbAPI().getDirectoryFiles(directory.getFullPath());
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

    }

    @Override
    public boolean isSearchSupported() {
        return false;
    }

    @Override
    public boolean isChangeEncodingSupported() {
        return false;
    }

    @Override
    public void open(FileProxy file) {

    }
}
