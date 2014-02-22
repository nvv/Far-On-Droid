package com.openfarmanager.android.core.network.datasource;

import com.microsoft.live.LiveOperation;
import com.openfarmanager.android.App;
import com.openfarmanager.android.core.network.skydrive.JsonKeys;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemScanner;
import com.openfarmanager.android.filesystem.SkyDriveFile;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.openfarmanager.android.utils.Extensions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Vlad Namashko
 */
public class SkyDriveDataSource implements DataSource {

    @Override
    public String getNetworkType() {
        return "SkyDrive";
    }

    @Override
    public NetworkEnum getNetworkTypeEnum() {
        return NetworkEnum.SkyDrive;
    }

    @Override
    public List<FileProxy> openDirectory(String path) {
        return App.sInstance.getSkyDriveApi().getDirectoryFiles(path);
    }

    @Override
    public void onUnlinkedAccount() {
        App.sInstance.getSkyDriveApi().deleteCurrentAccount();
    }

    @Override
    public String getPath(String path) {
        String pathAlias = App.sInstance.getSkyDriveApi().getFoldersAliases().get(path);
        return !isNullOrEmpty(pathAlias) ? pathAlias : path;
    }

    @Override
    public String getParentPath(String path) {
        return App.sInstance.getSkyDriveApi().findPathId(path);
    }

    @Override
    public void exitFromNetwork() {
        App.sInstance.getSkyDriveApi().getFoldersAliases().clear();
    }

    @Override
    public boolean isSearchSupported() {
        return true;
    }
}
