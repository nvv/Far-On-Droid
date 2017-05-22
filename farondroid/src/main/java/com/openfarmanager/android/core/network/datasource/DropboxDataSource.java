package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;
import android.util.Pair;

import com.annimon.stream.Stream;
import com.dropbox.core.v2.files.DeletedMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.DropboxFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemScanner;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;
import com.openfarmanager.android.utils.Extensions;
import com.openfarmanager.android.utils.FileUtilsExt;

import java.util.ArrayList;
import java.util.List;

import static com.openfarmanager.android.fragments.NetworkPanel.MSG_NETWORK_HIDE_PROGRESS;
import static com.openfarmanager.android.fragments.NetworkPanel.MSG_NETWORK_OPEN;
import static com.openfarmanager.android.fragments.NetworkPanel.MSG_NETWORK_SHOW_PROGRESS;

/**
 * @author Vlad Namashko
 */
public class DropboxDataSource extends RawPathDataSource {

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

    public NetworkPanel.DirectoryScanInfo openDirectory(FileProxy directory) {
        List<FileProxy> files = new ArrayList<FileProxy>();
        try {
            List<Metadata> metadataList = App.sInstance.getDropboxApi().listFiles(directory.getFullPath());
            Stream.of(metadataList).filter(entry -> !(entry instanceof DeletedMetadata)).forEach(entry -> files.add(new DropboxFile(entry)));
            FileSystemScanner.sInstance.sort(files);
        } catch (Exception e) {
            throw NetworkException.handleNetworkException(e);
        }

        return mDirectoryScanInfo.set(files, FileUtilsExt.getParentPath(directory.getParentPath()));
    }

    public void onUnlinkedAccount() {
        App.sInstance.getDropboxApi().deleteCurrentAccount();
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

        Extensions.runAsync(() -> {
            try {
                mHandler.sendMessage(mHandler.obtainMessage(MSG_NETWORK_OPEN,
                        new Pair<>(file, App.sInstance.getDropboxApi().getFileLink(file))));
            } catch (Exception e) {
                mHandler.sendEmptyMessage(MSG_NETWORK_HIDE_PROGRESS);
            }
        });
    }

    public FileProxy createFakeDirectory(String path) {
        return new DropboxFile(path);
    }
}
