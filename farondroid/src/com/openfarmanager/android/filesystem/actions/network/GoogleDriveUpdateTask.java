package com.openfarmanager.android.filesystem.actions.network;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.GoogleDriveFile;
import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;
import com.openfarmanager.android.model.TaskStatusEnum;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Vlad Namashko
 */
public class GoogleDriveUpdateTask extends NetworkActionTask {
    protected String mFileId;
    protected String mUpdateData;

    public GoogleDriveUpdateTask(BaseFileSystemPanel panel, OnActionListener listener, String fileId, String updateData) {
        super(panel.getFragmentManager(), panel, listener, new ArrayList<File>());
        mFileId = fileId;
        mNoProgress = true;
        mUpdateData = updateData;
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {
        // TODO: hack
        totalSize = 1;

        try {
            App.sInstance.getGoogleDriveApi().updateData(mFileId, mUpdateData);
        } catch (Exception e) {
            return TaskStatusEnum.ERROR_EXPORT_AS;
        }

        return TaskStatusEnum.OK;
    }
}
