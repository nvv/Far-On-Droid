package com.openfarmanager.android.core.network.datasource;

import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.NetworkEnum;

import java.util.List;

/**
 * @author Vlad Namashko
 */
public interface DataSource {

    String getNetworkType();

    NetworkEnum getNetworkTypeEnum();

    List<FileProxy> openDirectory(String path) throws RuntimeException;

    void onUnlinkedAccount();

    String getPath(String path);

    String getParentPath(String path);

    void exitFromNetwork();

    boolean isSearchSupported();

    void open(FileProxy file);
}
