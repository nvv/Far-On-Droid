package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FtpFile;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;
import com.openfarmanager.android.utils.FileUtilsExt;

/**
 * @author Vlad Namashko
 */
public class FtpDataSource extends RawPathDataSource {

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
    public NetworkPanel.DirectoryScanInfo openDirectory(FileProxy directory) throws NetworkException {
        return mDirectoryScanInfo.set(App.sInstance.getFtpApi().getDirectoryFiles(directory.getFullPath()),
                FileUtilsExt.getParentPath(directory.getParentPath()));
    }

    @Override
    public void onUnlinkedAccount() {
    }

    @Override
    public void exitFromNetwork() {
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
        return new FtpFile(path);
    }
}
