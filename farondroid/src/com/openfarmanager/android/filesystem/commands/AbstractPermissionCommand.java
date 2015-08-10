package com.openfarmanager.android.filesystem.commands;

import android.annotation.TargetApi;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

import com.openfarmanager.android.App;
import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.model.exeptions.SdcardPermissionException;

import java.io.File;
import java.util.List;

/**
 * @author Vlad Namashko.
 */
public abstract class AbstractPermissionCommand implements AbstractCommand {

    protected boolean checkVersion() {
        return Build.VERSION.SDK_INT >= 21;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected boolean checkForPermissionAndGetDestinationUrl (String sdCardPath, String currentPath) {
        List<UriPermission> persistedUriPermissions = App.sInstance.getContentResolver().getPersistedUriPermissions();
        if (persistedUriPermissions != null && persistedUriPermissions.size() > 0 && persistedUriPermissions.get(0).isWritePermission()) {
            UriPermission permission = persistedUriPermissions.get(0);
            Uri uri = permission.getUri();

            String subDir = currentPath.substring(sdCardPath.length());
            if (subDir.startsWith(File.separator)) {
                subDir = subDir.substring(1);
            }

            Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri,
                    DocumentsContract.getTreeDocumentId(Uri.parse(uri.getEncodedPath() +
                            subDir.replace("/", "%2F"))));

            return executeCommand(docUri);
        } else {
            throw new SdcardPermissionException();
        }
    }

    /**
     * Execute specific command with Storage Api.
     *
     * @param uri of destination directory.
     * @return <code>true</code> if command was successfully executed, <code>false</code> otherwise.
     */
    protected abstract boolean executeCommand(Uri uri);
}
