package com.openfarmanager.android.core.network.datasource;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemScanner;
import com.openfarmanager.android.filesystem.FtpFile;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPFile;

/**
 * @author Vlad Namashko
 */
public class FtpDataSource implements DataSource {

    @Override
    public String getNetworkType() {
        return "FTP";
    }

    @Override
    public NetworkEnum getNetworkTypeEnum() {
        return NetworkEnum.FTP;
    }

    @Override
    public List<FileProxy> openDirectory(String path) throws NetworkException {
        return App.sInstance.getFtpApi().getDirectoryFiles(path);
    }

    @Override
    public void onUnlinkedAccount() {
        //To change body of implemented methods use File | Settings | File Templates.
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
        return false;
    }
}
