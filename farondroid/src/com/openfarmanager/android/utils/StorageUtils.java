package com.openfarmanager.android.utils;

import android.annotation.TargetApi;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

import com.openfarmanager.android.App;
import com.openfarmanager.android.model.exeptions.SdcardPermissionException;

import java.io.File;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public class StorageUtils {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Uri checkForPermissionAndGetBaseUri() {
        List<UriPermission> persistedUriPermissions = App.sInstance.getContentResolver().getPersistedUriPermissions();
        if (persistedUriPermissions != null && persistedUriPermissions.size() > 0 && persistedUriPermissions.get(0).isWritePermission()) {
            UriPermission permission = persistedUriPermissions.get(0);
            return permission.getUri();
        } else {
            throw new SdcardPermissionException();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Uri getDestinationFileUri(Uri uri, String sdCardPath, String currentPath) {
        return getDestinationFileUri(uri, sdCardPath, currentPath, true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Uri getDestinationFileUri(Uri uri, String sdCardPath, String currentPath, boolean appendFileName) {

        if (!appendFileName) {
            currentPath = currentPath.substring(0, currentPath.lastIndexOf(File.separator));
        }

        String subDir = currentPath.substring(sdCardPath.length());
        if (subDir.startsWith(File.separator)) {
            subDir = subDir.substring(1);
        }

        return DocumentsContract.buildDocumentUriUsingTree(uri,
                DocumentsContract.getTreeDocumentId(Uri.parse(uri.getEncodedPath() +
                        subDir.replace("/", "%2F").replace(":", "%3A").replace(" ", "%20"))));
    }

    public static boolean checkVersion() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public static boolean checkUseStorageApi(String sdCardPath) {
        return !Extensions.isNullOrEmpty(sdCardPath) && checkVersion();
    }

}
