package com.openfarmanager.android.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.controllers.FileSystemController;
import com.openfarmanager.android.model.exeptions.InAppAuthException;
import com.openfarmanager.android.model.exeptions.InitYandexDiskException;
import com.openfarmanager.android.utils.Extensions;

import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;

/**
 * @author Vlad Namashko
 */
public class YandexDiskAuthDialog extends Dialog {

    private View mDialogView;
    private TextView mUserName;
    private TextView mPassword;
    private TextView mSaveAs;
    private TextView mError;

    private Handler mHandler;

    public YandexDiskAuthDialog(Context context, Handler handler) {
        super(context, R.style.Action_Dialog);
        mHandler = handler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogView = View.inflate(App.sInstance.getApplicationContext(), R.layout.dialog_yandex_disk_authentication, null);

        mUserName = (EditText) mDialogView.findViewById(R.id.yandex_username);
        mPassword = (EditText) mDialogView.findViewById(R.id.yandex_password);
        mSaveAs = (EditText) mDialogView.findViewById(R.id.yandex_save_as);

        mError = (TextView) mDialogView.findViewById(R.id.error);

        mDialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mDialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearError();
                if (!validate()) {
                    return;
                }

                connect();
            }
        });

        setContentView(mDialogView);
    }


    private boolean validate() {

        if (isNullOrEmpty(mUserName.getText().toString())) {
            setErrorMessage(App.sInstance.getString(R.string.error_user_empty));
            return false;
        }

        if (isNullOrEmpty(mPassword.getText().toString())) {
            setErrorMessage(App.sInstance.getString(R.string.error_password_empty));
            return false;
        }

        if (isNullOrEmpty(mSaveAs.getText().toString())) {
            setErrorMessage(App.sInstance.getString(R.string.error_save_as_empty));
            return false;
        }

        return true;
    }


    protected void clearError() {
        updateErrorState("", View.GONE);
    }

    protected void setErrorMessage(final String errorMessage) {
        updateErrorState(errorMessage, View.VISIBLE);
    }

    protected void setLoading(final boolean isLoading) {
        Message.obtain(mHandler, new Runnable() {
            @Override
            public void run() {
                mDialogView.findViewById(R.id.auth_form).setVisibility(isLoading ? View.GONE : View.VISIBLE);
                mDialogView.findViewById(R.id.progress_form).setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        }).sendToTarget();

    }

    protected void updateErrorState(final String errorMessage, final int visibility) {
        Message.obtain(mHandler, new Runnable() {
            @Override
            public void run() {
                mError.setVisibility(visibility);
                mError.setText(errorMessage);
            }
        }).sendToTarget();
    }

    protected void connect() {
        setLoading(true);
        Extensions.runAsync(getConnectRunnable());
    }

    protected Runnable getConnectRunnable() {
        return mConnectRunnable;
    }

    Runnable mConnectRunnable = new Runnable() {
        @Override
        public void run() {

            try {
                App.sInstance.getYandexDiskApi().connectAndSave(mUserName.getText().toString(),
                        mPassword.getText().toString(), mSaveAs.getText().toString());
            } catch (InitYandexDiskException e) {
                setErrorMessage(App.sInstance.getString(R.string.error_smb_wrong_credentials));
                setLoading(false);
                return;
            }

            dismiss();
            mHandler.sendEmptyMessage(FileSystemController.YANDEX_DISK_CONNECTED);
        }
    };

}
