package com.openfarmanager.android.filesystem.actions.multi.network;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;
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

    public MoveFromNetworkMultiTask(BaseFileSystemPanel panel, List<FileProxy> items, String destination) {
        super(panel, items, destination);
    }

    @Override
    public TaskStatusEnum handleSubTaskException(Exception e) {
        if (e instanceof NullPointerException) {
            return ERROR_FILE_NOT_EXISTS;
        } else if (e instanceof SmbAuthException || e instanceof WebdavException) {
            return createNetworkError(NetworkException.handleNetworkException(e));
        } else {
            return ERROR_DELETE_FILE;
        }
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
        return TaskStatusEnum.OK;
    }

    protected Runnable getActionRunnable() {
        return mActionRunnable;
    }

    private Runnable mActionRunnable = new Runnable() {
        @Override
        public void run() {
            setHeader(App.sInstance.getString(R.string.action_copy));
            TaskStatusEnum status = MoveFromNetworkMultiTask.super.doAction();

            if (hasSubTasks() && handleSubTasks(status)) {
                return;
            }

            setHeader(App.sInstance.getString(R.string.action_delete));
            status = doAction();

            if (hasSubTasks() && handleSubTasks(status)) {
                return;
            }

            onTaskDone(status);
        }
    };
}
