package com.openfarmanager.android.filesystem.actions.multi.network;

import android.content.Context;

import com.dropbox.client2.exception.DropboxException;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;
import com.yandex.disk.client.exceptions.WebdavException;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import jcifs.smb.SmbAuthException;

import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_DELETE_FILE;
import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_FILE_NOT_EXISTS;
import static com.openfarmanager.android.model.TaskStatusEnum.OK;
import static com.openfarmanager.android.model.TaskStatusEnum.createNetworkError;

/**
 * @author Vlad Namashko
 */
public class DeleteFromNetworkTask extends NetworkActionMultiTask {

    protected List<FileProxy> mItems;

    public DeleteFromNetworkTask(BaseFileSystemPanel panel, OnActionListener listener, List<FileProxy> items) {
        super(panel, listener, null);
        mItems = items;
    }

    @Override
    protected void calculateSize() {
        mTotalSize = mItems.size();
    }

    @Override
    public TaskStatusEnum doAction() {

        final NetworkApi api = getApi();
        for (final FileProxy file : mItems) {
            if (isCancelled()) {
                break;
            }

            runSubTaskAsynk(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    api.delete(file);
                    return null;
                }
            }, file);
        }

        mCurrentFile = getProgressText();
        updateProgress();

        return OK;
    }

    @Override
    public TaskStatusEnum handleSubTaskException(Exception e) {
        if (e instanceof NullPointerException) {
            return ERROR_FILE_NOT_EXISTS;
        } else if (e instanceof DropboxException || e instanceof SmbAuthException || e instanceof WebdavException) {
            return createNetworkError(NetworkException.handleNetworkException(e));
        } else {
            return ERROR_DELETE_FILE;
        }
    }

    @Override
    public void onSubTaskDone(Future future) {
        super.onSubTaskDone(future);
        mDoneSize++;
        mCurrentFile = getProgressText();
        updateProgress();
    }

    @Override
    protected String getTag() {
        return "DeleteFromNetworkTask";
    }
}
