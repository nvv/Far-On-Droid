package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;
import android.util.Pair;

import com.dropbox.client2.DropboxAPI;
import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.DropboxFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemScanner;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;
import com.openfarmanager.android.utils.Extensions;

import java.util.ArrayList;
import java.util.List;

import static com.openfarmanager.android.fragments.NetworkPanel.MSG_NETWORK_HIDE_PROGRESS;
import static com.openfarmanager.android.fragments.NetworkPanel.MSG_NETWORK_OPEN;
import static com.openfarmanager.android.fragments.NetworkPanel.MSG_NETWORK_SHOW_PROGRESS;

/**
 * @author Vlad Namashko
 */
public class DropboxDataSource implements DataSource {

    public Handler mHandler;

    public DropboxDataSource(Handler handler) {
        mHandler = handler;
    }

    public String getNetworkType() {
        return "Dropbox";
    }

    @Override
    public NetworkEnum getNetworkTypeEnum() {
        return NetworkEnum.Dropbox;
    }

    public List<FileProxy> openDirectory(FileProxy directory) {
        List<FileProxy> files = new ArrayList<FileProxy>();
        DropboxAPI.Entry currentNode;
        try {
            currentNode = App.sInstance.getDropboxApi().metadata(directory.getFullPath(), -1, null, true, null);
            for (DropboxAPI.Entry entry : currentNode.contents) {
                files.add(new DropboxFile(entry));
            }
            FileSystemScanner.sInstance.sort(files);
        } catch (Exception e) {
            throw NetworkException.handleNetworkException(e);
        }

        return files;
    }

    public void onUnlinkedAccount() {
        App.sInstance.getDropboxApi().deleteCurrentAccount();
    }

    @Override
    public String getPath(String path) {
        return path;
    }

    @Override
    public String getParentPath(String path) {
        return path;
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
    public void open(final FileProxy file) {
        mHandler.sendEmptyMessage(MSG_NETWORK_SHOW_PROGRESS);

        Extensions.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_NETWORK_OPEN,
                            new Pair<FileProxy, String>(file, App.sInstance.getDropboxApi().media(file.getFullPath(), false).url)));
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(MSG_NETWORK_HIDE_PROGRESS);
                }
            }
        });
    }
}
