package com.openfarmanager.android.fragments;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.FileSystemAdapter;
import com.openfarmanager.android.core.archive.MimeTypes;
import com.openfarmanager.android.utils.CustomFormatter;
import com.openfarmanager.android.utils.FileUtilsExt;

import java.io.File;
import java.util.Date;

/**
 * @author Vlad Namashko
 */
public class InfoDialog extends BaseDialog {

    private View mRootView;
    private View mProgress;
    private View mFileDetails;
    private View mFolderDetails;

    private LoadDataTask mLoadDataTask;

    public static InfoDialog newInstance(String filePath) {
        InfoDialog infoDialog = new InfoDialog();
        Bundle args = new Bundle();
        args.putString("filePath", filePath);
        infoDialog.setArguments(args);
        return infoDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Action_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(getSafeString(R.string.app_name));

        mRootView = inflater.inflate(R.layout.dialog_file_info, container, false);
        mProgress = mRootView.findViewById(R.id.loading);
        mFileDetails = mRootView.findViewById(R.id.details_file);
        mFolderDetails = mRootView.findViewById(R.id.details_folder);

        mRootView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRunningTask();
                dismiss();
            }
        });

        cancelRunningTask();
        mLoadDataTask = new LoadDataTask();
        mLoadDataTask.execute();

        return mRootView;
    }

    private void cancelRunningTask() {
        if (mLoadDataTask != null) {
            mLoadDataTask.cancel(true);
            mLoadDataTask = null;
        }
    }

    private class LoadDataTask extends AsyncTask<Void, Void, FileUtilsExt.DirectoryScanResult> {

        private Exception mExecutionException;
        private File mCurrentFile;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.setVisibility(View.VISIBLE);
            mFileDetails.setVisibility(View.GONE);
            mFolderDetails.setVisibility(View.GONE);
        }

        @Override
        protected FileUtilsExt.DirectoryScanResult doInBackground(Void... params) {
            try {
                mCurrentFile = new File(getArguments().getString("filePath"));
                return FileUtilsExt.getDirectoryDetails(mCurrentFile);
            } catch (Exception e) {
                mExecutionException = e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(FileUtilsExt.DirectoryScanResult result) {

            if (getActivity() == null || getActivity().isFinishing() || mCurrentFile == null) {
                return;
            }

            String permissions = "";
            permissions += mCurrentFile.canRead() ? "r" : "-";
            permissions += mCurrentFile.canWrite() ? "w" : "-";

            mProgress.setVisibility(View.GONE);
            if (mExecutionException != null) {
                TextView error = ((TextView) mRootView.findViewById(R.id.error));
                error.setText(getSafeString(R.string.error_quick_view_error_while_calculating_detailes, mExecutionException.getLocalizedMessage()));
                error.setVisibility(View.VISIBLE);
            } else {
                setSafeValue(R.id.file_name, mCurrentFile.getName());
                setSafeValue(R.id.folders, Long.toString(result.directories));
                setSafeValue(R.id.files, Long.toString(result.files));
                setSafeValue(R.id.size, CustomFormatter.formatBytes(result.filesSize));
                setSafeValue(R.id.last_modified, FileSystemAdapter.sDateFormat.format(new Date(mCurrentFile.lastModified())));
                setSafeValue(R.id.permissions, permissions);
                setSafeValue(R.id.mime_type, MimeTypes.getMimeType(MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(mCurrentFile).toString())));

                mFileDetails.setVisibility(mCurrentFile.isDirectory() ? View.GONE : View.VISIBLE);
                mFolderDetails.setVisibility(mCurrentFile.isDirectory() ? View.VISIBLE : View.GONE);
            }
        }

        private View getCurrentVisibleView() {
            return mCurrentFile.isDirectory() ? mFolderDetails : mFileDetails;
        }

        private void setSafeValue(int view_id, String value) {
            TextView textView = (TextView) getCurrentVisibleView().findViewById(view_id);
            if (textView != null) {
                textView.setText(value);
            }
        }

    }

}
