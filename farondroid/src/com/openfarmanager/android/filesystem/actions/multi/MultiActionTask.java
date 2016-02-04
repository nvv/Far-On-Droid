package com.openfarmanager.android.filesystem.actions.multi;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.openfarmanager.android.App;
import com.openfarmanager.android.BuildConfig;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.Settings;
import com.openfarmanager.android.core.network.smb.SmbAPI;
import com.openfarmanager.android.dialogs.FileActionProgressDialog;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.utils.StorageUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import jcifs.smb.SmbAuthException;

import static com.openfarmanager.android.utils.Extensions.*;

/**
 * @author Vlad Namashko
 */
public abstract class MultiActionTask {

    private static final int MSG_PROGRESS = 0;
    private static final int MSG_POST_RESULT = 1;
    private static final int MSG_SET_HEADER = 2;

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
    private Map<Future, String> mSubTasksAsynkLabels = new HashMap<>();
    private StringBuilder mActiveSubTasksListBuilder = new StringBuilder();

    private int mLabelType;

    public MultiActionTask(final Context context, OnActionListener listener, List<File> items) {
        mItems = items;
        mListener = listener;
        mTaskResult = new TaskResult();
        mTaskResult.task = this;
        mLabelType = App.sInstance.getSettings().getMultiActionLabelType();
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
        runAsync(getActionRunnable());
    }

    public void runSubTaskAsynk(Callable subTask, File file) {
        Future future = runAsync(subTask);
        mSubTasksAsynk.add(future);
        mSubTasksAsynkLabels.put(future, file.getName());
    }

    public void runSubTaskAsynk(Callable subTask, FileProxy file) {
        Future future = runAsync(subTask);
        mSubTasksAsynk.add(future);
        mSubTasksAsynkLabels.put(future, file.getName());
    }

    protected String getProgressText() {
        if (mLabelType == Settings.MULTI_ACTION_LABEL_TYPE_FILES_NUM) {
            return getActiveSubTaskStatus();
        } else {
            return getActiveSubTaskFiles();
        }
    }

    private String getActiveSubTaskFiles() {
        mActiveSubTasksListBuilder.setLength(0);
        for (String file : mSubTasksAsynkLabels.values()) {
            mActiveSubTasksListBuilder.append(file).append("  ");
        }
        return mActiveSubTasksListBuilder.toString();
    }

    private String getActiveSubTaskStatus() {
        return App.sInstance.getResources().getString(R.string.processing_files, mSubTasksAsynkLabels.size());
    }

    protected void setHeader(String header) {
        mTaskResult.header = header;
        getHandler().obtainMessage(MSG_SET_HEADER, mTaskResult).sendToTarget();
    }

    protected void publishProgress() {
        mTaskResult.fileName = mCurrentFile == null ? "" : mCurrentFile;
        getHandler().obtainMessage(MSG_PROGRESS, mTaskResult).sendToTarget();
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

    /**
     * Update progress on dialog.
     *
     * In of <code>mTotalSize</code> equals 0 most likely we have indeterminate and should update
     * only title text on dialog (if text is not empty).
     */
    protected void updateProgress() {
        if (mTotalSize > 0) {
            publishProgress((int) (100 * mDoneSize / mTotalSize));
        } else if (!isNullOrEmpty(mCurrentFile)) {
            publishProgress();
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
            onSubTaskDone(future);
        }
    }

    protected Runnable getActionRunnable() {
        return mActionRunnable;
    }

    protected boolean hasSubTasks() {
        return mSubTasksAsynk.size() > 0;
    }

    private Runnable mActionRunnable = new Runnable() {
        @Override
        public void run() {
            calculateSize();
            TaskStatusEnum status = doAction();

            if (hasSubTasks() && handleSubTasks(status)) {
                return;
            }

            onTaskDone(status);
        }
    };

    /**
     * Process async sub tasks (if present): wait for execution and handle errors (if any).
     *
     * @param status result of common action execution.
     * @return <code>true</code> if task was finished with error and doesn't need to be handled outside,<code>false</code> otherwise.
     */
    protected boolean handleSubTasks(TaskStatusEnum status) {
        if (status == TaskStatusEnum.OK) {
            try {
                for (Future future : mSubTasksAsynk) {
                    future.get();
                    onSubTaskDone(future);
                }
            } catch (ExecutionException e) {
                handleExecutionException(e);
                return true;
            } catch (Exception e) {
                onTaskDone(handleSubTaskException(e));
                return true;
            }
        } else {
            for (Future future : mSubTasksAsynk) {
                future.cancel(true);
            }
        }

        return false;
    }

    private void handleExecutionException(ExecutionException e) {
        // attempt to extract execution exception
        String[] messages = e.getCause().toString().split(":");
        try {
            Class clazz = Class.forName(messages[0].trim());
            // special case for access denied on LAN
            onTaskDone(clazz.equals(SmbAuthException.class) && messages[1].trim().equals(SmbAPI.ACCESS_DENIED) ?
                    TaskStatusEnum.ERROR_ACCESS_DENIED : handleSubTaskException((Exception) clazz.newInstance()));
        } catch (Exception ee) {
            onTaskDone(handleSubTaskException(e));
        }
    }

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

    public void onSubTaskDone(Future future) {
        mSubTasksAsynkLabels.remove(future);
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
                case MSG_SET_HEADER:
                    taskResult.task.mProgressDialog.setHeader(taskResult.header);
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
        String header;
        TaskStatusEnum status;
    }

}
