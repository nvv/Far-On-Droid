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
    private ProgressBar mProgress;
    private boolean mIndeterminate;
    private OnDismissListener mOnDismissListener;

    public FileActionProgressDialog(Context context, OnDismissListener listener) {
        super(context, R.style.Action_Dialog);
        mOnDismissListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = View.inflate(App.sInstance.getApplicationContext(), R.layout.dialog_action_progress, null);
        mTitle = (TextView) view.findViewById(android.R.id.title);
        mProgress = (ProgressBar) view.findViewById(android.R.id.progress);
        mProgress.setIndeterminate(mIndeterminate);

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                mOnDismissListener.onDismiss(FileActionProgressDialog.this);
            }
        });

        setContentView(view);
    }

    public void updateProgress(String currentFile, int p) {
        mTitle.setText(currentFile);
        mProgress.setProgress(p);
    }

    public void setIndeterminate(boolean value) {
        mIndeterminate = value;
    }

}
