package com.openfarmanager.android.filesystem.actions.network;

import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;

import java.util.ArrayList;

import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_FILE_NOT_EXISTS;
import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_RENAME_FILE;
import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_WRONG_DESTINATION_FILE_NAME;
import static com.openfarmanager.android.model.TaskStatusEnum.OK;
import static com.openfarmanager.android.model.TaskStatusEnum.createNetworkError;

public class RenameOnNetworkTask extends NetworkActionTask {

    private String mDestinationFileName;
    private FileProxy mSrcFile;

    public RenameOnNetworkTask(BaseFileSystemPanel panel, String destinationName, FileProxy file) {
        super(panel, new ArrayList<>());
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
        } catch (Exception e) {
            return ERROR_RENAME_FILE;
        }

        return OK;
    }
}