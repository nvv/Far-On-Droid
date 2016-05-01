package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.WebDavFile;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.utils.FileUtilsExt;

/**
 * @author Vlad Namashko
 */
public class WebDavDataSource extends RawPathDataSource {

    public WebDavDataSource(Handler handler) {
        mHandler = handler;
    }

    @Override
    public String getNetworkType() {
        return "WebDav";
    }

    @Override
    public NetworkEnum getNetworkTypeEnum() {
        return NetworkEnum.WebDav;
    }

    @Override
    public NetworkPanel.DirectoryScanInfo openDirectory(FileProxy directory) throws RuntimeException {
        return mDirectoryScanInfo.set(App.sInstance.getWebDavApi().getDirectoryFiles(directory.getFullPath()),
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
        return false;
    }

    @Override
    public boolean isChangeEncodingSupported() {
        return false;
    }

    @Override
    public void open(FileProxy file) {

    }

    @Override
    public FileProxy createFakeDirectory(String path) {
        return new WebDavFile(path);
    }
}
