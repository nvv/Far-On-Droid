package com.openfarmanager.android.filesystem.actions.network;

import android.content.Intent;
import android.net.Uri;

import com.openfarmanager.android.App;
import com.openfarmanager.android.core.network.dropbox.DropboxAPI;
import com.openfarmanager.android.filesystem.DropboxFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;
import com.openfarmanager.android.model.TaskStatusEnum;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Vlad Namashko
 */
public class DropboxTask extends NetworkActionTask {

    public static final int TASK_SHARE = 1000;

    protected DropboxFile mDropboxFile;
    protected int mTask;

    public DropboxTask(BaseFileSystemPanel panel, DropboxFile file, int task) {
        super(panel, new ArrayList<>());
        mDropboxFile = file;
        mNoProgress = true;
        mTask = task;
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... params) {
        mTotalSize = 1;

        if (mTask == TASK_SHARE)

            try {
                final String dropboxLink = App.sInstance.getDropboxApi().share(mDropboxFile.getFullPath());
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_TEXT, dropboxLink);
                App.sInstance.startActivity(intent);

            } catch (Exception e) {
                return TaskStatusEnum.ERROR_EXPORT_AS;
            }

        return TaskStatusEnum.OK;
    }

}
