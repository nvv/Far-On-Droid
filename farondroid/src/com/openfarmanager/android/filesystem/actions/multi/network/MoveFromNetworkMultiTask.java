package com.openfarmanager.android.filesystem.actions.multi.network;

import android.content.Context;

import com.dropbox.client2.exception.DropboxException;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;
import com.yandex.disk.client.exceptions.WebdavException;

import java.util.List;
import java.util.concurrent.Callable;

import jcifs.smb.SmbAuthException;

import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_DELETE_FILE;
import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_FILE_NOT_EXISTS;
import static com.openfarmanager.android.model.TaskStatusEnum.createNetworkError;

/**
 * @author Vlad Namashko
 */
public class MoveFromNetworkMultiTask extends CopyFromNetworkMultiTask {

    public MoveFromNetworkMultiTask(Context context, NetworkEnum networkType, OnActionListener listener, List<FileProxy> items, String destination) {
        super(context, networkType, listener, items, destination);
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
    public TaskStatusEnum doAction() {
        final NetworkApi api = getApi();
        for (final FileProxy file : mItems) {
            runSubTaskAsynk(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    api.delete(file);
                    return null;
                }
            });
        }
        return TaskStatusEnum.OK;
    }

    protected Runnable getActionRunnable() {
        return mActionRunnable;
    }

    private Runnable mActionRunnable = new Runnable() {
        @Override
        public void run() {
            calculateSize();
            TaskStatusEnum status = MoveFromNetworkMultiTask.super.doAction();

            if (hasSubTasks() && handleSubTasks(status)) {
                return;
            }

            status = doAction();

            if (hasSubTasks() && handleSubTasks(status)) {
                return;
            }

            onTaskDone(status);
        }
    };
}
