package com.openfarmanager.android.core.network.datasource;

import com.mediafire.sdk.MediaFire;
import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.NetworkEnum;

import java.util.List;

/**
 * author: Vlad Namashko
 */
public class MediaFireDataSource implements DataSource {

    @Override
    public String getNetworkType() {
        return "MediaFire";
    }

    @Override
    public NetworkEnum getNetworkTypeEnum() {
        return NetworkEnum.MediaFire;
    }

    @Override
    public List<FileProxy> openDirectory(String path) throws RuntimeException {
        return null;
    }

    @Override
    public void onUnlinkedAccount() {

    }

    @Override
    public String getPath(String path) {
        return "";
    }

    @Override
    public String getParentPath(String path) {
        return "";
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
    public void open(FileProxy file) {

    }
}
