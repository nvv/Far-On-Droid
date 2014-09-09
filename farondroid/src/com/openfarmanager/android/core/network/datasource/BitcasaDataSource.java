package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;

import com.bitcasa.client.datamodel.FileMetaData;
import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.NetworkEnum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;

/**
 * @author Vlad Namashko
 */
public class BitcasaDataSource implements DataSource {

    private Handler mHandler;

    public BitcasaDataSource(Handler handler) {
        mHandler = handler;
    }

    @Override
    public String getNetworkType() {
        return "Bitcasa";
    }

    @Override
    public NetworkEnum getNetworkTypeEnum() {
        return NetworkEnum.Bitcasa;
    }

    @Override
    public List<FileProxy> openDirectory(String path) throws RuntimeException {
        return App.sInstance.getBitcasaApi().getDirectoryFiles(path);
    }

    @Override
    public void onUnlinkedAccount() {

    }

    @Override
    public String getPath(String path) {
        String pathAlias = App.sInstance.getBitcasaApi().getFoldersAliases().get(path);
        return !isNullOrEmpty(pathAlias) ? pathAlias : path;
    }

    @Override
    public String getParentPath(String path) {
        return App.sInstance.getBitcasaApi().findPathId(path);
    }

    @Override
    public void exitFromNetwork() {

    }

    @Override
    public boolean isSearchSupported() {
        return false;
    }

    @Override
    public void open(FileProxy file) {

    }
}
