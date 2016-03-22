package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;
import android.util.Pair;

import com.mediafire.sdk.api.responses.data_models.FileInfo;
import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.SkyDriveFile;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;

import java.util.List;

import static com.openfarmanager.android.fragments.NetworkPanel.MSG_NETWORK_OPEN;
import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;

/**
 * @author Vlad Namashko
 */
public class SkyDriveDataSource extends IdPathDataSource {

    public SkyDriveDataSource(Handler handler) {
        mHandler = handler;
    }

    @Override
    public String getNetworkType() {
        return "OneDrive";
    }

    @Override
    public NetworkEnum getNetworkTypeEnum() {
        return NetworkEnum.SkyDrive;
    }

    @Override
    protected List<FileProxy> getDirectoryFiles(FileProxy directory) {
        return App.sInstance.getSkyDriveApi().getDirectoryFiles(directory.getId(), directory.getFullPathRaw());
    }

    @Override
    protected FileProxy requestFileInfo(String id) {
        return App.sInstance.getSkyDriveApi().getFileInfo(id);
    }

    @Override
    public void onUnlinkedAccount() {
        App.sInstance.getSkyDriveApi().deleteCurrentAccount();
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
        return false;
    }

    @Override
    public void open(FileProxy file) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_NETWORK_OPEN, new Pair<>(file, ((SkyDriveFile) file).getSource())));
    }
}
