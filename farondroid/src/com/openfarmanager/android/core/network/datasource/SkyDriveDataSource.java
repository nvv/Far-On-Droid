package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;
import android.util.Pair;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.SkyDriveFile;
import com.openfarmanager.android.model.NetworkEnum;

import java.util.List;

import static com.openfarmanager.android.fragments.NetworkPanel.MSG_NETWORK_OPEN;
import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;

/**
 * @author Vlad Namashko
 */
public class SkyDriveDataSource implements DataSource {

    public Handler mHandler;

    public SkyDriveDataSource(Handler handler) {
        mHandler = handler;
    }

    @Override
    public String getNetworkType() {
        return "SkyDrive";
    }

    @Override
    public NetworkEnum getNetworkTypeEnum() {
        return NetworkEnum.SkyDrive;
    }

    @Override
    public List<FileProxy> openDirectory(String path) {
        return App.sInstance.getSkyDriveApi().getDirectoryFiles(path);
    }

    @Override
    public void onUnlinkedAccount() {
        App.sInstance.getSkyDriveApi().deleteCurrentAccount();
    }

    @Override
    public String getPath(String path) {
        String pathAlias = App.sInstance.getSkyDriveApi().getFoldersAliases().get(path);
        return !isNullOrEmpty(pathAlias) ? pathAlias : path;
    }

    @Override
    public String getParentPath(String path) {
        return App.sInstance.getSkyDriveApi().findPathId(path);
    }

    @Override
    public void exitFromNetwork() {
        App.sInstance.getSkyDriveApi().getFoldersAliases().clear();
    }

    @Override
    public boolean isSearchSupported() {
        return true;
    }

    @Override
    public void open(FileProxy file) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_NETWORK_OPEN, new Pair<FileProxy, String>(file, ((SkyDriveFile) file).getSource())));
    }
}
