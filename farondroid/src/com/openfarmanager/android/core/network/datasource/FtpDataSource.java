package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;

import java.util.List;

/**
 * @author Vlad Namashko
 */
public class FtpDataSource implements DataSource {

    public Handler mHandler;

    public FtpDataSource(Handler handler) {
        mHandler = handler;
    }

    @Override
    public String getNetworkType() {
        return "FTP";
    }

    @Override
    public NetworkEnum getNetworkTypeEnum() {
        return NetworkEnum.FTP;
    }

    @Override
    public List<FileProxy> openDirectory(FileProxy directory) throws NetworkException {
        return App.sInstance.getFtpApi().getDirectoryFiles(directory.getFullPath());
    }

    @Override
    public void onUnlinkedAccount() {
        //To change body of implemented methods use File | Settings | File Templates.
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
        return true;
    }

    @Override
    public void open(FileProxy file) {

    }
}
