package com.openfarmanager.android.core.network.datasource;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.utils.Extensions;

import java.util.List;

/**
 * author: Vlad Namashko
 */
public class GoogleDriveDataSource implements DataSource {

    @Override
    public String getNetworkType() {
        return "Google Drive";
    }

    @Override
    public NetworkEnum getNetworkTypeEnum() {
        return NetworkEnum.GoogleDrive;
    }

    @Override
    public List<FileProxy> openDirectory(String path) throws RuntimeException {
        return App.sInstance.getGoogleDriveApi().getDirectoryFiles(path);
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
        return false;
    }
}
