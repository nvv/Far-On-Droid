package com.openfarmanager.android.filesystem.commands;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.filesystem.actions.RootTask;
import com.openfarmanager.android.fragments.ErrorDialog;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.utils.SystemUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Vlad Namashko.
 */
public class CreateNewCommand extends AbstractPermissionCommand {

    private MainPanel mPanel;
    private boolean mIsCreateDirectory;
    private String mDestinationFileName;

    public CreateNewCommand(MainPanel panel) {
        mPanel = panel;
    }

    @Override
    public void execute(final Object... args) {
        mIsCreateDirectory = (Boolean) args[2];
        mDestinationFileName = (String) args[1];
        File destination = new File(mPanel.getCurrentDir(), mDestinationFileName);
        boolean result;
        try {
            String sdCardPath = SystemUtils.getExternalStorage(destination.getAbsolutePath());
            result = sdCardPath != null && checkVersion() ?
                    checkForPermissionAndGetDestinationUrl(sdCardPath, mPanel.getCurrentPath()) :
                    createFileRawApi(mIsCreateDirectory, destination);
        } catch (IOException e) {
            result = false;
        }

        if (!result) {
            try {
                ErrorDialog.newInstance(App.sInstance.getString(R.string.error_cannot_create_file,
                        mDestinationFileName)).show(mPanel.fragmentManager(), "errorDialog");
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


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected boolean executeCommand(Uri uri) {
        Uri fileUri = DocumentsContract.createDocument(App.sInstance.getContentResolver(),
                uri, mIsCreateDirectory ? DocumentsContract.Document.MIME_TYPE_DIR : "",
                mDestinationFileName);
        return fileUri != null;
    }
}
