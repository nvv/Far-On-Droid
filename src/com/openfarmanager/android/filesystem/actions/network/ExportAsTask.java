package com.openfarmanager.android.filesystem.actions.network;

import android.support.v4.app.FragmentManager;

import com.openfarmanager.android.App;
import com.openfarmanager.android.model.TaskStatusEnum;

import java.io.File;
import java.util.ArrayList;

/**
 * author: Vlad Namashko
 */
public class ExportAsTask extends NetworkActionTask {

    private final static byte[] BUFFER = new byte[256 * 1024];

    protected String mDestination;
    protected String mDownloadLink;

    public ExportAsTask(FragmentManager fragmentManager, OnActionListener listener, String downloadLink, String destination) {
        super(fragmentManager, listener, new ArrayList<File>());
        mDestination = destination;
        mNoProgress = true;
        mDownloadLink = downloadLink;
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {
        // TODO: hack
        totalSize = 1;

        try {
            App.sInstance.getGoogleDriveApi().download(mDownloadLink, mDestination);
        } catch (Exception e) {
            return TaskStatusEnum.ERROR_EXPORT_AS;
        }

        return TaskStatusEnum.OK;
    }

}
