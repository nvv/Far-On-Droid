package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;
import android.util.Pair;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.YandexDiskFile;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.utils.Extensions;
import com.openfarmanager.android.utils.FileUtilsExt;

import static com.openfarmanager.android.fragments.NetworkPanel.MSG_NETWORK_OPEN;

/**
 * @author Vlad Namashko
 */
public class YandexDiskDataSource extends RawPathDataSource {

    public YandexDiskDataSource(Handler handler) {
        mHandler = handler;
    }

    @Override
    public String getNetworkType() {
        return "YandexDisk";
    }

    @Override
    public NetworkEnum getNetworkTypeEnum() {
        return NetworkEnum.YandexDisk;
    }

    @Override
    public NetworkPanel.DirectoryScanInfo openDirectory(FileProxy directory) throws RuntimeException {
        return mDirectoryScanInfo.set(App.sInstance.getYandexDiskApi().getDirectoryFiles(directory.getFullPath()),
                FileUtilsExt.getParentPath(directory.getParentPath()));
    }

    @Override
    public void onUnlinkedAccount() {
        App.sInstance.getDropboxApi().deleteCurrentAccount();
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

    public FileProxy createFakeDirectory(String path) {
        return new YandexDiskFile(path);
    }

    @Override
    public void open(FileProxy file) {

        YandexDiskFile diskFile = (YandexDiskFile) file;

        if (!Extensions.isNullOrEmpty(diskFile.getPublicUrl())) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_NETWORK_OPEN, new Pair<>(file, diskFile.getPublicUrl())));
        }
    }
}
