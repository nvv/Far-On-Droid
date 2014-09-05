package com.openfarmanager.android.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.openfarmanager.android.R;
import com.openfarmanager.android.utils.ParcelableWrapper;

import java.io.Serializable;

public abstract class EditViewDialog extends BaseDialog {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Action_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(getSafeString(R.string.app_name));
        final View view = inflater.inflate(getLayout(), container, false);

        restoreSettings(view);

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                if (getListener() != null) {
                    saveSettings(view);
                    dismiss();
                    handleAction(view);
                }
            }
        });

        return view;
    }

    protected EditDialogListener getListener() {
        //noinspection unchecked
        return ((ParcelableWrapper<EditDialogListener>) getArguments().getParcelable("listener")).value;
    }

    protected abstract void restoreSettings(View view);

    protected abstract void saveSettings(View view);

    protected abstract int getLayout();

    protected abstract void handleAction(final View view);

    public static interface EditDialogListener extends Serializable {

        public void doSearch(String pattern, boolean caseSensitive, boolean wholeWords, boolean regularExpression);

        public void doReplace(String pattern, String replaceTo, boolean caseSensitive, boolean wholeWords, boolean regularExpression);

        public void goTo(int position, int unit);
    }
}