package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;
import android.util.Pair;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.utils.Extensions;

import java.util.List;

import static com.openfarmanager.android.fragments.NetworkPanel.MSG_NETWORK_OPEN;

/**
 * author: Vlad Namashko
 */
public class GoogleDriveDataSource implements DataSource {

    public Handler mHandler;

    public GoogleDriveDataSource(Handler handler) {
        mHandler = handler;
    }

    @Override
    public String getNetworkType() {
        return "Google Drive";
    }

    @Override
    public NetworkEnum getNetworkTypeEnum() {
        return NetworkEnum.GoogleDrive;
    }

    @Override
    public List<FileProxy> openDirectory(FileProxy directory) throws RuntimeException {
        return App.sInstance.getGoogleDriveApi().getDirectoryFiles(directory.getId(), directory.getFullPathRaw());
    }

    @Override
    public void onUnlinkedAccount() {

    }

    @Override
    public String getPath(String path) {
        String pathAlias = App.sInstance.getGoogleDriveApi().getFoldersAliases().get(path);
        return !Extensions.isNullOrEmpty(pathAlias) ? pathAlias : path;
    }

    @Override
    public String getParentPath(String path) {
        return App.sInstance.getGoogleDriveApi().findPathId(path);
    }

    @Override
    public void exitFromNetwork() {
        App.sInstance.getGoogleDriveApi().getFoldersAliases().clear();
    }

    @Override
    public boolean isSearchSupported() {
        return true;
    }

    @Override
    public boolean isChangeEncodingSupported() {
        return false;
    }

    @Override
    public void open(FileProxy file) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_NETWORK_OPEN, new Pair<FileProxy, String>(file,
                App.sInstance.getGoogleDriveApi().getDownloadLink(file))));
    }
}
