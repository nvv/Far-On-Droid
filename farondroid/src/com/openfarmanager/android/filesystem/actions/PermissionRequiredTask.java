package com.openfarmanager.android.filesystem.actions;

import android.annotation.TargetApi;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.support.v4.app.FragmentManager;

import com.openfarmanager.android.App;
import com.openfarmanager.android.model.exeptions.SdcardPermissionException;
import com.openfarmanager.android.utils.Extensions;

import java.io.File;
import java.util.List;

/**
 * author: vnamashko
 */
public abstract class PermissionRequiredTask extends FileActionTask {

    public PermissionRequiredTask(FragmentManager fragmentManager, OnActionListener listener, List<File> items) {
        super(fragmentManager, listener, items);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected Uri checkForPermissionAndGetBaseUri() {
        List<UriPermission> persistedUriPermissions = App.sInstance.getContentResolver().getPersistedUriPermissions();
        if (persistedUriPermissions != null && persistedUriPermissions.size() > 0 && persistedUriPermissions.get(0).isWritePermission()) {
            UriPermission permission = persistedUriPermissions.get(0);
            return permission.getUri();
        } else {
            throw new SdcardPermissionException();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected Uri checkForPermissionAndGetDestinationUrl(Uri uri, String sdCardPath, String currentPath) {
        return checkForPermissionAndGetDestinationUrl(uri, sdCardPath, currentPath, true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected Uri checkForPermissionAndGetDestinationUrl(Uri uri, String sdCardPath, String currentPath, boolean appendFileName) {

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

    protected boolean checkVersion() {
        return Build.VERSION.SDK_INT >= 21;
    }

    protected boolean checkUseStorageApi(String sdCardPath) {
        return !Extensions.isNullOrEmpty(sdCardPath) && checkVersion();
    }

}
