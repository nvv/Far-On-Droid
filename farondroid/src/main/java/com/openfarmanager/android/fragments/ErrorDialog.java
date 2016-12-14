package com.openfarmanager.android.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.openfarmanager.android.R;

/**
 * Error dialog
 */
public class ErrorDialog extends BaseDialog {

    public static DialogFragment newInstance(String string) {
        ErrorDialog dialog = new ErrorDialog();
        Bundle args = new Bundle();
        args.putString("message", string);
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
        getDialog().setTitle(getSafeString(R.string.app_name));
        View mView = inflater.inflate(R.layout.dialog_error, container, false);

        mView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        ((TextView) mView.findViewById(R.id.text)).setText(getArguments().getString("message"));
        return mView;
    }
}
