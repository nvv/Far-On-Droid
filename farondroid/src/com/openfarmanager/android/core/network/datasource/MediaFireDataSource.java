package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;

import java.util.List;

import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;

/**
 * author: Vlad Namashko
 */
public class MediaFireDataSource extends IdPathDataSource {

    public MediaFireDataSource(Handler handler) {
        mHandler = handler;
    }

    @Override
    public String getNetworkType() {
        return "MediaFire";
    }

    @Override
    public NetworkEnum getNetworkTypeEnum() {
        return NetworkEnum.MediaFire;
    }

    @Override
    protected List<FileProxy> getDirectoryFiles(FileProxy directory) {
        return App.sInstance.getMediaFireApi().openDirectory(directory.getId(), directory.getFullPathRaw());
    }

    @Override
    public FileProxy requestFileInfo(String id) {
        return App.sInstance.getMediaFireApi().getFileInfo(id);
    }

    @Override
    public void onUnlinkedAccount() {

    }

    @Override
    public void exitFromNetwork() {
        App.sInstance.getMediaFireApi().endSession();
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
