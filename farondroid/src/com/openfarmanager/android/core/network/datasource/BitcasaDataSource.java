package com.openfarmanager.android.core.network.datasource;

import android.os.Handler;
import android.util.Pair;

import com.bitcasa.client.datamodel.FileMetaData;
import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.BitcasaFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.NetworkEnum;

import java.util.List;

import static com.openfarmanager.android.fragments.NetworkPanel.MSG_NETWORK_OPEN;
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
        App.sInstance.getBitcasaApi().getFoldersAliases().clear();
    }

    @Override
    public boolean isSearchSupported() {
        return false;
    }

    @Override
    public void open(FileProxy file) {
        FileMetaData metaData = ((BitcasaFile) file).getMetaData();

        try {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_NETWORK_OPEN, new Pair<FileProxy, String>(file,
                    App.sInstance.getBitcasaApi().getClient().getDownloadLink(metaData))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
