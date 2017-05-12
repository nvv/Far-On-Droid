package com.openfarmanager.android.filesystem.actions;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;

import com.annimon.stream.Stream;
import com.openfarmanager.android.App;
import com.openfarmanager.android.core.bus.RxBus;
import com.openfarmanager.android.core.bus.TaskCancelledEvent;
import com.openfarmanager.android.core.bus.TaskErrorEvent;
import com.openfarmanager.android.core.bus.TaskOkEvent;
import com.openfarmanager.android.dialogs.FileActionProgressDialog;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.utils.FileUtilsExt;
import com.openfarmanager.android.utils.StorageUtils;

import java.io.File;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public abstract class FileActionTask extends AsyncTask<Void, Integer, TaskStatusEnum> {

    protected final static byte[] BUFFER = new byte[2048 * 1024];

    protected List<File> mItems;
    protected FileActionProgressDialog mProgressDialog;
    protected Context mContext;
    protected String mCurrentFile;

    protected long mTotalSize = 0;
    protected long mDoneSize = 0;

    protected boolean mNoProgress;

    protected String mSdCardPath;
    protected Uri mBaseUri;
    protected boolean mUseStorageApi;

    protected int mInvokedOnPanel;

    /**
     * Task start time in milliseconds. For debug purposes.
     */
    protected long mTaskStartTime;

    public FileActionTask(Context context, int invokedOnPanel, List<File> items) {
        mContext = context;
        mInvokedOnPanel = invokedOnPanel;
        mItems = items;
    }

    protected FileActionTask() {
    }

    @Override
    protected void onPreExecute() {

        Stream.of(mItems).forEach(file -> mTotalSize += FileUtilsExt.sizeOf(file));

        mProgressDialog = new FileActionProgressDialog(mContext, mProgressDialogDismissListener);
        mProgressDialog.setIndeterminate(mNoProgress);
        mProgressDialog.show();

        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        String currentName = mCurrentFile == null ? "" : mCurrentFile;
        mProgressDialog.updateProgress(currentName, values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(TaskStatusEnum status) {
        try {
            mProgressDialog.dismiss();
        } catch (Exception ignore) { }

        if (status == TaskStatusEnum.OK) {
            RxBus.getInstance().postEvent(new TaskOkEvent(mInvokedOnPanel));
        } else {
            RxBus.getInstance().postEvent(new TaskErrorEvent(mInvokedOnPanel).setStatus(status).setExtra(getExtra()));
        }
        super.onPostExecute(status);
    }

    protected Object getExtra() {
        return "";
    }

    protected void updateProgress() {
        if (mTotalSize > 0) {
            publishProgress((int) (100 * mDoneSize / mTotalSize));
        }
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
            cancel(true);
//            mListener.onActionFinish(TaskStatusEnum.CANCELED);
            RxBus.getInstance().postEvent(new TaskCancelledEvent(mInvokedOnPanel));
        }
    };
}
