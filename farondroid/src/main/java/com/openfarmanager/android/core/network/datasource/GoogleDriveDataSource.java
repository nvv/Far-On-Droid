package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;
import android.util.Pair;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FakeFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.googledrive.model.File;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.utils.Extensions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.openfarmanager.android.fragments.NetworkPanel.MSG_NETWORK_OPEN;

/**
 * author: Vlad Namashko
 */
public class GoogleDriveDataSource extends IdPathDataSource {

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
    protected List<FileProxy> getDirectoryFiles(FileProxy directory) {
        return App.sInstance.getGoogleDriveApi().getDirectoryFiles(directory.getId(), directory.getFullPathRaw());
    }

    @Override
    public FileProxy requestFileInfo(String id) {
        return App.sInstance.getGoogleDriveApi().getFileInfo(id);
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
        return false;
    }

    @Override
    public void open(FileProxy file) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_NETWORK_OPEN, new Pair<>(file,
                App.sInstance.getGoogleDriveApi().getDownloadLink(file))));
    }

}
