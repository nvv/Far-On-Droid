package com.openfarmanager.android.core.network;

import com.openfarmanager.android.core.network.dropbox.DropboxAPI;
import com.openfarmanager.android.core.network.ftp.FtpAPI;
import com.openfarmanager.android.core.network.ftp.SftpAPI;
import com.openfarmanager.android.core.network.googledrive.GoogleDriveApi;
import com.openfarmanager.android.core.network.mediafire.MediaFireApi;
import com.openfarmanager.android.core.network.skydrive.SkyDriveAPI;
import com.openfarmanager.android.core.network.smb.SmbAPI;
import com.openfarmanager.android.core.network.yandexdisk.YandexDiskApi;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;

import org.json.JSONObject;

/**
 * @author Vlad Namashko
 */
public class NetworkAccountManager {

    public NetworkAccount createNetworkAccount(long accountId, String user,
                                              String authData, int networkType) throws Exception {

        switch (NetworkEnum.fromOrdinal(networkType)) {
            case Dropbox:
                return new DropboxAPI.DropboxAccount(accountId, user, new JSONObject(authData));
            case SkyDrive:
                return new SkyDriveAPI.SkyDriveAccount(accountId, user, new JSONObject(authData));
            case FTP:
                return new FtpAPI.FtpAccount(accountId, user, new JSONObject(authData));
            case SMB:
                return new SmbAPI.SmbAccount(accountId, user, new JSONObject(authData));
            case YandexDisk:
                return new YandexDiskApi.YandexDiskAccount(accountId, user, new JSONObject(authData));
            case GoogleDrive:
                return new GoogleDriveApi.GoogleDriveAccount(accountId, user, new JSONObject(authData));
            case MediaFire:
                return new MediaFireApi.MediaFireAccount(accountId, user, authData);
            case SFTP:
                return new SftpAPI.SftpAccount(accountId, user, new JSONObject(authData));
        }

        return null;
    }

    public NetworkEnum getNetworkType(int networkType) {
        return NetworkEnum.fromOrdinal(networkType);
    }

}
