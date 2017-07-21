package com.openfarmanager.android.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.fragments.MainPanel;

/**
 * author: Vlad Namashko
 */
public abstract class BaseFileDialog extends Dialog {

    protected View mDialogView;
    protected TextView mError;
    protected Handler mHandler;
    protected MainPanel mInactivePanel;

    protected EditText mDestination;

    protected Button mOkButton;

    public BaseFileDialog(Context context, Handler handler, MainPanel inactivePanel) {
        super(context, R.style.Action_Dialog);
        mHandler = handler;
        mInactivePanel = inactivePanel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mDialogView = View.inflate(getContext(), getContentView(), null);
        setContentView(mDialogView);

        mDestination = (EditText) mDialogView.findViewById(R.id.destination);
        mError = (TextView) mDialogView.findViewById(R.id.error);

        mDialogView.findViewById(R.id.cancel).setOnClickListener(v -> dismiss());

        mOkButton = (Button) mDialogView.findViewById(R.id.ok);
        mOkButton.setOnClickListener(v -> {
            clearError();
            if (!validate()) {
                return;
            }

            execute();

            dismiss();
        });
    }

    protected void clearError() {
        updateErrorState("", View.GONE);
    }

    protected void setErrorMessage(final String errorMessage) {
        updateErrorState(errorMessage, View.VISIBLE);
    }

    protected void updateErrorState(final String errorMessage, final int visibility) {
        mError.setVisibility(visibility);
        mError.setText(errorMessage);
    }

    protected void updateLabels(String title, String destLabel, String destDefaultValue) {
        ((TextView) mDialogView.findViewById(R.id.current_action)).setText(title);
        ((TextView) mDialogView.findViewById(R.id.destination_label)).setText(destLabel);
        ((EditText) mDialogView.findViewById(R.id.destination)).setText(destDefaultValue);
    }

    /**
     * getstring using Application instance instead of Activity, which throw exception.
     *
     * @param resId Resource id for the string
     */
    protected final String getSafeString(int resId) {
        return App.sInstance.getString(resId);
    }

    /**
     * getstring using Application instance instead of Activity, which throw exception.
     *
     * @param resId Resource id for the string
     */
    protected final String getSafeString(int resId, Object... formatArgs) {
        return App.sInstance.getString(resId, formatArgs);
    }

    public abstract int getContentView();

    protected abstract boolean validate();

    protected abstract void execute();

}
