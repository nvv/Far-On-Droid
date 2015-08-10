package com.openfarmanager.android.filesystem.commands;

import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.filesystem.actions.RootTask;
import com.openfarmanager.android.fragments.ErrorDialog;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.model.exeptions.SdcardPermissionException;
import com.openfarmanager.android.utils.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Vlad Namashko.
 */
public class CreateNewCommand implements AbstractCommand {

    private MainPanel mPanel;

    public CreateNewCommand(MainPanel panel) {
        mPanel = panel;
    }

    @Override
    public void execute(final Object... args) {
        boolean createDirectory = (Boolean) args[2];
        File destination = new File(mPanel.getCurrentDir(), (String) args[1]);
        boolean result;
        try {
            String sdCardPath = SystemUtils.getExternalStorage(destination.getAbsolutePath());
            if (sdCardPath != null && Build.VERSION.SDK_INT >= 21) {
                List<UriPermission> persistedUriPermissions = App.sInstance.getContentResolver().getPersistedUriPermissions();
                if (persistedUriPermissions != null && persistedUriPermissions.size() > 0 && persistedUriPermissions.get(0).isWritePermission()) {
                    UriPermission permission = persistedUriPermissions.get(0);
                    Uri uri = permission.getUri();

                    String currentPath = mPanel.getCurrentPath();
                    String subDir = currentPath.substring(sdCardPath.length());
                    if (subDir.startsWith(File.separator)) {
                        subDir = subDir.substring(1);
                    }

                    Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri,
                            DocumentsContract.getTreeDocumentId(Uri.parse(uri.getEncodedPath() +
                                    subDir.replace("/", "%2F"))));

                    Uri fileUri = DocumentsContract.createDocument(App.sInstance.getContentResolver(),
                            docUri, createDirectory ? DocumentsContract.Document.MIME_TYPE_DIR : "",
                            (String) args[1]);
                    result = fileUri != null;
                } else {
                    throw new SdcardPermissionException();
                }
            } else {
                result = createFileRawApi(createDirectory, destination);
            }
        } catch (IOException e) {
            result = false;
        }

        if (!result) {
            try {
                ErrorDialog.newInstance(App.sInstance.getString(R.string.error_cannot_create_file,
                        (String) args[1])).show(mPanel.fragmentManager(), "errorDialog");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mPanel.invalidatePanels((MainPanel) args[0]);
    }

    private boolean createFileRawApi(boolean createDirectory, File destination) throws IOException {
        boolean result;
        File parentFile = destination.getParentFile();
        boolean isRootRequired = !parentFile.canRead() || !parentFile.canWrite();
        result = isRootRequired ? RootTask.create(destination, createDirectory) :
                createDirectory ?
                        destination.mkdir() : destination.createNewFile();
        return result;
    }


}
