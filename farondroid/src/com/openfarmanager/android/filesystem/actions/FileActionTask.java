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

    protected final static byte[] BUFFER = new byte[512 * 1024];

    protected List<File> mItems;
    protected FileActionProgressDialog mProgressDialog;
    protected FragmentManager mFragmentManager;
    protected String mCurrentFile;
    protected OnActionListener mListener;

    protected long totalSize = 0;
    protected long doneSize = 0;

    protected boolean mNoProgress;

    protected String mSdCardPath;
    protected Uri mBaseUri;
    protected boolean mUseStorageApi;

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

    protected boolean createDirectoryIfNotExists(String dir) {
        File outputDir = new File(dir);
        return outputDir.exists() || (mUseStorageApi ? StorageUtils.mkDir(mBaseUri, mSdCardPath, outputDir) : outputDir.mkdirs());
    }

    protected boolean createDirectoryIfNotExists(File outputDir) {
        return outputDir.exists() || (mUseStorageApi ? StorageUtils.mkDir(mBaseUri, mSdCardPath, outputDir) : outputDir.mkdirs());
    }

}
