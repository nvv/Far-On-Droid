package com.openfarmanager.android.core.network.datasource;

import com.dropbox.client2.DropboxAPI;
import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.DropboxFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemScanner;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public class DropboxDataSource implements DataSource {

    public String getNetworkType() {
        return "Dropbox";
    }

    @Override
    public NetworkEnum getNetworkTypeEnum() {
        return NetworkEnum.Dropbox;
    }

    public List<FileProxy> openDirectory(String path) {
        List<FileProxy> files = new ArrayList<FileProxy>();
        DropboxAPI.Entry currentNode;
        try {
            currentNode = App.sInstance.getDropboxApi().metadata(path, -1, null, true, null);
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
}
