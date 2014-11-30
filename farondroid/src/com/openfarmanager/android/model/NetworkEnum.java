package com.openfarmanager.android.model;

import android.content.res.Resources;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;

/**
 * @author Vlad Namashko
 */
public enum  NetworkEnum {
    Dropbox, SkyDrive, FTP, SMB, YandexDisk, GoogleDrive, Bitcasa;

    public static NetworkEnum[] sOrderedItems = {FTP, SMB, Dropbox, GoogleDrive, SkyDrive, YandexDisk};

    public static String getNetworkLabel(NetworkEnum networkEnum) {
        switch (networkEnum) {
            case FTP: return "FTP";
            case SMB: return App.sInstance.getString(R.string.local_network);
            case Dropbox: return "Dropbox";
            case SkyDrive: return "SkyDrive";
            case GoogleDrive: return "Google Drive";
            case YandexDisk: return "Yandex Disk";
            case Bitcasa: return "Bitcasa";
            default: return "Dropbox";
        }
    }

    public static NetworkEnum fromOrdinal(int ordinal) {
        if (ordinal == -1) {
            return null;
        }

        return NetworkEnum.values()[ordinal];
    }

    public static NetworkEnum[] valuesList() {
        return sOrderedItems;
    }
}
