package com.openfarmanager.android.filesystem.actions.multi;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.openfarmanager.android.BuildConfig;
import com.openfarmanager.android.dialogs.FileActionProgressDialog;
import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.utils.StorageUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static com.openfarmanager.android.utils.Extensions.*;

/**
 * @author Vlad Namashko
 */
public abstract class MultiActionTask {

    private static final int MSG_PROGRESS = 0;
    private static final int MSG_POST_RESULT = 1;

    protected final static byte[] BUFFER = new byte[512 * 1024];

    private static InternalHandler sHandler;

    protected List<File> mItems;
    protected FileActionProgressDialog mProgressDialog;
    protected String mCurrentFile;
    protected OnActionListener mListener;

    protected long mTotalSize = 0;
    protected long mDoneSize = 0;

    protected String mSdCardPath;
    protected Uri mBaseUri;
    protected boolean mUseStorageApi;

    protected boolean mIsCancelled;

    /**
     * Task start time in milliseconds. For debug purposes.
     */
    private long mTaskStartTime;

    private TaskResult mTaskResult;
    private List<Future> mSubTasksAsynk = new LinkedList<>();

    public MultiActionTask(final Context context, OnActionListener listener, List<File> items) {
        mItems = items;
        mListener = listener;
        mTaskResult = new TaskResult();
        mTaskResult.task = this;
        init(context);
    }

    protected MultiActionTask() {
    }

    protected void init(Context context) {
        mProgressDialog = new FileActionProgressDialog(context, mProgressDialogDismissListener);
        mProgressDialog.setIndeterminate(isIndeterminate());
        mProgressDialog.show();
    }

    protected void calculateSize() {
        for (File file : mItems) {
            try {
                mTotalSize += FileUtils.sizeOf(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void execute() {
        if (BuildConfig.DEBUG) {
            mTaskStartTime = System.currentTimeMillis();
        }
        runAsync(mActionRunnable);
    }

    public void runSubTaskAsynk(Callable subTask) {
        mSubTasksAsynk.add(runAsync(subTask));
    }

    protected void publishProgress(final int value) {
        mTaskResult.fileName = mCurrentFile == null ? "" : mCurrentFile;
        mTaskResult.progress = value;

        getHandler().obtainMessage(MSG_PROGRESS, mTaskResult).sendToTarget();
    }

    protected void onTaskDone(final TaskStatusEnum status) {
        if (BuildConfig.DEBUG) {
            Log.d(getTag(), "execution time = " + (System.currentTimeMillis() - mTaskStartTime));
        }
        mTaskResult.status = status;
        getHandler().obtainMessage(MSG_POST_RESULT, mTaskResult).sendToTarget();
    }

    protected void updateProgress() {
        if (mTotalSize > 0) {
            publishProgress((int) (100 * mDoneSize / mTotalSize));
        }
    }

    protected boolean isCancelled() {
        return mIsCancelled;
    }

    protected boolean createDirectoryIfNotExists(String dir) {
        File outputDir = new File(dir);
        return outputDir.exists() || (mUseStorageApi ? StorageUtils.mkDir(mBaseUri, mSdCardPath, outputDir) : outputDir.mkdirs());
    }

    protected boolean createDirectoryIfNotExists(File outputDir) {
        return outputDir.exists() || (mUseStorageApi ? StorageUtils.mkDir(mBaseUri, mSdCardPath, outputDir) : outputDir.mkdirs());
    }

    private DialogInterface.OnDismissListener mProgressDialogDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            cancel();
            mListener.onActionFinish(TaskStatusEnum.CANCELED);
        }
    };

    public void cancel() {
        mIsCancelled = true;
        for (Future future : mSubTasksAsynk) {
            if (!future.isDone()) {
                future.cancel(true);
            }
            onSubTaskDone();
        }
    }

    private Runnable mActionRunnable = new Runnable() {
        @Override
        public void run() {
            calculateSize();
            TaskStatusEnum status = doAction();

            if (mSubTasksAsynk.size() > 0) {
                // task successful, wait for sub-tasks
                if (status == TaskStatusEnum.OK) {
                    try {
                        for (Future future : mSubTasksAsynk) {
                            if (!future.isDone()) {
                                future.get();
                            }
                            onSubTaskDone();
                        }
                    } catch (Exception e) {
                        onTaskDone(handleSubTaskException(e));
                    }
                } else {
                    for (Future future : mSubTasksAsynk) {
                        future.cancel(true);
                    }
                }
            }

            onTaskDone(status);
        }
    };

    private static InternalHandler getHandler() {
        synchronized (MultiActionTask.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler();
            }
            return sHandler;
        }
    }

    protected boolean isIndeterminate() {
        return false;
    }

    protected String getTag() {
        return "MultiActionTask";
    }

    public void onSubTaskDone() {
    }

    public abstract TaskStatusEnum doAction();

    public abstract TaskStatusEnum handleSubTaskException(Exception e);

    private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {

            TaskResult taskResult = (TaskResult) msg.obj;
            switch (msg.what) {
                case MSG_POST_RESULT:
                    try {
                        taskResult.task.mProgressDialog.dismiss();
                    } catch (Exception ignore) { }

                    if (taskResult.task.mListener != null) {
                        taskResult.task.mListener.onActionFinish(taskResult.status);
                    }
                    break;
                case MSG_PROGRESS:
                    taskResult.task.mProgressDialog.updateProgress(taskResult.fileName, taskResult.progress);
                    break;
            }
        }
    }

    private static class TaskResult {
        MultiActionTask task;
        int progress;
        String fileName;
        TaskStatusEnum status;
    }

}
