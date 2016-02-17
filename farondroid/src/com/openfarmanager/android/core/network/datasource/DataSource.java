package com.openfarmanager.android.core.network.datasource;

import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;

/**
 * @author Vlad Namashko
 */
public interface DataSource {

    String getNetworkType();

    NetworkEnum getNetworkTypeEnum();

    NetworkPanel.DirectoryScanInfo openDirectory(FileProxy directory) throws RuntimeException;

    void onUnlinkedAccount();

    String getPath(String path);

    String getParentPath(String path);

    void exitFromNetwork();

    boolean isSearchSupported();

    boolean isChangeEncodingSupported();

    void open(FileProxy file);
}
