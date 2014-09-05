package com.openfarmanager.android.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.openfarmanager.android.R;
import com.openfarmanager.android.model.FileActionEnum;
import com.openfarmanager.android.utils.ParcelableWrapper;

import java.io.Serializable;

/**
 * Dialog with question and available actions : 'Yes'/'No'
 */
public class YesNoDialog extends BaseDialog {

    public static DialogFragment newInstance(String string, YesNoDialogListener listener, boolean isErrorDialog) {
        YesNoDialog dialog = new YesNoDialog();
        Bundle args = new Bundle();
        args.putString("message", string);
        args.putParcelable("listener", new ParcelableWrapper<YesNoDialogListener>(listener));
        args.putBoolean("isErrorDialog", isErrorDialog);
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
        View view = inflater.inflate(R.layout.yes_no_dialog, container, false);

        if (getArguments().getBoolean("isErrorDialog")) {
            view.findViewById(R.id.root_frame).setBackgroundResource(R.color.error_red);
        }

        view.findViewById(R.id.button_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YesNoDialogListener listener = getListener();
                if (listener != null) {
                    listener.yes();
                }
                dismiss();
            }
        });

        view.findViewById(R.id.button_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YesNoDialogListener listener = getListener();
                if (listener != null) {
                    listener.no();
                }
                dismiss();
            }
        });

        ((TextView) view.findViewById(R.id.text)).setText(getArguments().getString("message"));
        return view;
    }

    private YesNoDialogListener getListener() {
        return ((ParcelableWrapper<YesNoDialogListener>) getArguments().getParcelable("listener")).value;
    }

    public static interface YesNoDialogListener extends Serializable {
        void yes();
        void no();
    }
}
