package com.openfarmanager.android.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;

/**
 * @author Vlad Namashko
 */
public class FileActionProgressDialog extends Dialog {

    private TextView mTitle;
    private TextView mHeader;
    private ProgressBar mProgress;
    private boolean mIndeterminate;
    private OnDismissListener mOnDismissListener;

    public FileActionProgressDialog(Context context, OnDismissListener listener) {
        super(context, R.style.Action_Dialog);
        mOnDismissListener = listener;
        setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = View.inflate(App.sInstance.getApplicationContext(), R.layout.dialog_multi_action_progress, null);
        mTitle = (TextView) view.findViewById(android.R.id.title);
        mHeader = (TextView) view.findViewById(R.id.header);
        mProgress = (ProgressBar) view.findViewById(android.R.id.progress);
        mProgress.setIndeterminate(mIndeterminate);

        view.findViewById(R.id.cancel).setOnClickListener(view1 -> {
            dismiss();
            mOnDismissListener.onDismiss(FileActionProgressDialog.this);
        });

        setContentView(view);
    }

    public void updateProgress(String currentFile, int progress) {
        mTitle.setText(currentFile);
        mProgress.setProgress(progress);
    }

    public void setHeader(String header) {
        mHeader.setVisibility(View.VISIBLE);
        mHeader.setText(header);
    }

    public void setIndeterminate(boolean value) {
        mIndeterminate = value;
    }

}
