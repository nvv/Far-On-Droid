package com.openfarmanager.android.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.openfarmanager.android.R;
import com.openfarmanager.android.utils.ParcelableWrapper;

import java.io.Serializable;

/**
 * Current action progress dialog with total progress bar and currently processing file
 */
public class FileActionProgressDialog extends BaseDialog {
    private TextView mTitle;
    private ProgressBar mProgress;
    private boolean mIndeterminate;

    public static FileActionProgressDialog newInstance(OnDismissListener listener) {
        FileActionProgressDialog dialog = new FileActionProgressDialog();
        Bundle args = new Bundle();
        args.putParcelable("listener", new ParcelableWrapper<OnDismissListener>(listener));
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Action_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_action_progress, container, false);

        mTitle = (TextView) view.findViewById(android.R.id.title);
        mProgress = (ProgressBar) view.findViewById(android.R.id.progress);
        mProgress.setIndeterminate(mIndeterminate);

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

                try {
                    //noinspection unchecked
                    OnDismissListener listener = ((ParcelableWrapper<OnDismissListener>)
                            getArguments().getParcelable("listener")).value;
                    if (listener != null) {
                        listener.onDismiss();
                    }
                } catch (NullPointerException ignore) {}
            }
        });

        return view;
    }

    public void updateProgress(String currentFile, int p){
        mTitle.setText(currentFile);
        mProgress.setProgress(p);
    }

    public void setIndeterminate(boolean value) {
        mIndeterminate = value;
    }

    public static interface OnDismissListener extends Serializable {
        void onDismiss();
    }
}
