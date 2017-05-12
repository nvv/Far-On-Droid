package com.openfarmanager.android.filesystem.actions.network;

import android.support.v4.app.FragmentManager;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;
import com.openfarmanager.android.model.TaskStatusEnum;

import java.io.File;
import java.util.ArrayList;

/**
 * author: Vlad Namashko
 */
public class ExportAsTask extends NetworkActionTask {

    protected String mDestination;
    protected String mDownloadLink;

    public ExportAsTask(BaseFileSystemPanel panel, String downloadLink, String destination) {
        super(panel, new ArrayList<>());
        mDestination = destination;
        mNoProgress = true;
        mDownloadLink = downloadLink;
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {
        // TODO: hack
        mTotalSize = 1;

        try {
            App.sInstance.getGoogleDriveApi().download(mDownloadLink, mDestination);
        } catch (Exception e) {
            return TaskStatusEnum.ERROR_EXPORT_AS;
        }

        return TaskStatusEnum.OK;
    }

}
