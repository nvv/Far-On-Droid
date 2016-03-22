package com.openfarmanager.android.filesystem.actions.network;

import android.support.v4.app.FragmentManager;
import com.dropbox.client2.exception.DropboxException;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.actions.FileActionTask;
import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;

import java.io.File;
import java.util.ArrayList;

import static com.openfarmanager.android.model.TaskStatusEnum.*;

public class RenameOnNetworkTask extends NetworkActionTask {

private String mDestinationFileName;
    private FileProxy mSrcFile;

    public RenameOnNetworkTask(BaseFileSystemPanel panel, OnActionListener listener,
                               FileProxy file, String destinationName) {
        mFragmentManager = panel.getFragmentManager();
        mListener = listener;
        mItems = new ArrayList<>();
        mSrcFile = file;
        mDestinationFileName = destinationName;
        initNetworkPanelInfo(panel);
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {

        if (mDestinationFileName == null || mDestinationFileName.trim().equals("")) {
            return ERROR_WRONG_DESTINATION_FILE_NAME;
        }

        try {
            if (!getApi().rename(mSrcFile, mSrcFile.getParentPath() +
                    (mSrcFile.getParentPath().endsWith("/") ? "" : "/") + mDestinationFileName)) {
                return ERROR_RENAME_FILE;
            }
        } catch (NullPointerException e) {
            return ERROR_FILE_NOT_EXISTS;
        } catch (DropboxException e) {
            return createNetworkError(NetworkException.handleNetworkException(e));
        } catch (Exception e) {
            return ERROR_RENAME_FILE;
        }

        return OK;
    }
}