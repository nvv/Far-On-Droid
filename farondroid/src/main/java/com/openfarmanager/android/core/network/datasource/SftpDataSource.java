package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.SftpFile;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.utils.FileUtilsExt;

/**
 * @author Vlad Namashko
 */
public class SftpDataSource extends RawPathDataSource {

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
        return mDirectoryScanInfo.set(App.sInstance.getSftpApi().getDirectoryFiles(directory.getFullPath()),
                FileUtilsExt.getParentPath(directory.getParentPath()));
    }

    @Override
    public void onUnlinkedAccount() {

    }

    @Override
    public void exitFromNetwork() {
        App.sInstance.getSftpApi().closeChannel();
    }

    @Override
    public boolean isSearchSupported() {
        return true;
    }

    @Override
    public boolean isChangeEncodingSupported() {
        return true;
    }

    @Override
    public void open(FileProxy file) {

    }

    @Override
    public FileProxy createFakeDirectory(String path) {
        return new SftpFile(path);
    }
}
