package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;

import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;

/**
 * author: Vlad Namashko
 */
public class MediaFireDataSource implements DataSource {

    private Handler mHandler;

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
    public NetworkPanel.DirectoryScanInfo openDirectory(FileProxy directory) throws RuntimeException {
        //return App.sInstance.getMediaFireApi().openDirectory(directory.getId());
        return null;
    }

    @Override
    public void onUnlinkedAccount() {

    }

    @Override
    public String getPath(String path) {
        String pathAlias = App.sInstance.getMediaFireApi().getFoldersAliases().get(path);
        return !isNullOrEmpty(pathAlias) ? pathAlias : path;
    }

    @Override
    public String getParentPath(String path) {
        return App.sInstance.getMediaFireApi().findPathId(path);
    }

    @Override
    public void exitFromNetwork() {
        App.sInstance.getMediaFireApi().endSession();
        App.sInstance.getMediaFireApi().getFoldersAliases().clear();
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
