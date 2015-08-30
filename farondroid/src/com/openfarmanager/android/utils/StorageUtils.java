package com.openfarmanager.android.utils;

import android.annotation.TargetApi;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

import com.openfarmanager.android.App;
import com.openfarmanager.android.model.exeptions.SdcardPermissionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static OutputStream getStorageOutputFileStream(File destination, String sdCardPath) throws FileNotFoundException {
        Uri baseUri = checkForPermissionAndGetBaseUri();
        return getStorageOutputFileStream(destination, baseUri, sdCardPath);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static OutputStream getStorageOutputFileStream(File destination, Uri baseUri, String sdCardPath) throws FileNotFoundException {
        String originalName = destination.getName();
        String newName = destination.getName().replace(":", "_");
        String destinationFile = destination.getAbsolutePath().replace(originalName, newName);

        Uri outputFileUri = getDestinationFileUri(baseUri, sdCardPath, destinationFile, false);
        if (!destination.exists()) {
            DocumentsContract.createDocument(App.sInstance.getContentResolver(),
                    outputFileUri, "",
                    newName);
        }
        return App.sInstance.getContentResolver().openOutputStream(
                getDestinationFileUri(baseUri, sdCardPath, destinationFile));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean mkDir(Uri baseUri, String sdCardPath, File outputDir) {

        // ensure that parent path exists.
        File parentFile = outputDir.getParentFile();
        while (parentFile != null && !parentFile.exists()) {
            mkDir(baseUri, sdCardPath, parentFile);
        }

        return DocumentsContract.createDocument(App.sInstance.getContentResolver(),
                getDestinationFileUri(baseUri, sdCardPath, outputDir.getAbsolutePath(), false),
                DocumentsContract.Document.MIME_TYPE_DIR, outputDir.getName()) != null;
    }
}
