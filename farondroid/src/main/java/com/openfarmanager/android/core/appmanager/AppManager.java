package com.openfarmanager.android.core.appmanager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import com.openfarmanager.android.App;

import java.io.File;
import java.util.List;

/**
 * High level wrapper over <code>PackageManager</code>.
 * Provides additional functionality to deal with installed packages and file types.
 *
 * @see android.content.pm.PackageManager
 * @author Vlad Namashko
 */
public class AppManager {

    private PackageManager mPackageManager;

    public AppManager() {
        mPackageManager = App.sInstance.getPackageManager();
    }

    /**
     * Extract extension and mime type from file name. Creates new intent (with ActionView) with extracted
     * data so android can find applications to handle this file.
     *
     * @param item item to be opened
     * @return filtered intent provided with mime type.
     */
    public Intent getFilteredIntent(File item) {
        Uri uri = Uri.fromFile(item);
        String ext = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);

        return intent;
    }

    /**
     * Get activities which can handle current intent.
     *
     * @param intent to be handles.
     * @return list with information about activities.
     */
    public List<ResolveInfo> getIntentActivities(Intent intent) {
        return mPackageManager.queryIntentActivities(intent, 0);
    }

    /**
     * Returns all activities which can handle any mime type.
     *
     * @param item item to be opened.
     * @return list with information about activities.
     */
    public List<ResolveInfo> getAllCallableActivities(File item) {
        return getCallableActivities(item, "*/*");
    }

    /**
     * Returns all activities which can handle requested mime type.
     *
     * @param item item to be opened
     * @param contentType content type which <code>item</code> must be opened with.
     * @return list with information about activities.
     */
    public List<ResolveInfo> getCallableActivities(File item, String contentType) {
        return mPackageManager.queryIntentActivities(
                new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(item), contentType), 0);
    }
}
