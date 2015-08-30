package com.openfarmanager.android.filesystem.actions;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import com.openfarmanager.android.fragments.FileActionProgressDialog;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.utils.StorageUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 * User: sokhotnyi
 */
public abstract class FileActionTask extends AsyncTask<Void, Integer, TaskStatusEnum> {

    protected List<File> mItems;
    protected FileActionProgressDialog mProgressDialog;
    protected FragmentManager mFragmentManager;
    protected String mCurrentFile;
    protected OnActionListener mListener;

    protected long totalSize = 0;
    protected long doneSize = 0;

    protected boolean mNoProgress;

    public FileActionTask(FragmentManager fragmentManager, OnActionListener listener, List<File> items) {
        mItems = items;
        mFragmentManager = fragmentManager;
        mListener = listener;
    }

    public FileActionTask() {
    }

    @Override
    protected void onPreExecute() {

        for (File file : mItems) {
            try {
                totalSize += FileUtils.sizeOf(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mProgressDialog = FileActionProgressDialog.newInstance(new FileActionProgressDialog.OnDismissListener() {
            @Override
            public void onDismiss() {
                cancel(true);
                mListener.onActionFinish(TaskStatusEnum.CANCELED);
            }
        });

        mProgressDialog.setIndeterminate(mNoProgress);

        mProgressDialog.show(mFragmentManager, "progress_dialog");
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

        if (mListener != null) {
            mListener.onActionFinish(status);
        }
        super.onPostExecute(status);
    }

    protected void updateProgress() {
        if (totalSize > 0) {
            publishProgress((int) (100 * doneSize / totalSize));
        }
    }

    protected boolean createDirectory(File outputDir, String sdCardPath, boolean useStorageApi, Uri baseUri) {
        return useStorageApi ? StorageUtils.mkDir(baseUri, sdCardPath, outputDir) : outputDir.mkdirs();
    }

    public static interface OnActionListener {
        void onActionFinish(TaskStatusEnum status);
    }
}
