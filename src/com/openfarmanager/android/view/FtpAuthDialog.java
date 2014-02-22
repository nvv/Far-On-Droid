package com.openfarmanager.android.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.controllers.FileSystemController;
import com.openfarmanager.android.model.exeptions.InAppAuthException;
import com.openfarmanager.android.utils.Extensions;

import static com.openfarmanager.android.utils.Extensions.*;

/**
 * author: Vlad Namashko
 */
public class FtpAuthDialog extends Dialog {

    private Handler mHandler;
    private View mDialogView;
    private TextView mError;
    private EditText mServer;
    private EditText mPort;
    private RadioGroup mMode;
    private EditText mUserName;
    private EditText mPassword;
    private CheckBox mAnonymous;

    public FtpAuthDialog(Context context, Handler handler) {
        super(context, R.style.Action_Dialog);
        mHandler = handler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogView = View.inflate(App.sInstance.getApplicationContext(), R.layout.dialog_ftp_authentication, null);

        mServer = (EditText) mDialogView.findViewById(R.id.ftp_server);
        mPort = (EditText) mDialogView.findViewById(R.id.ftp_port);
        mMode = (RadioGroup) mDialogView.findViewById(R.id.ftp_mode);
        mAnonymous = (CheckBox) mDialogView.findViewById(R.id.ftp_anonymous);
        mUserName = (EditText) mDialogView.findViewById(R.id.ftp_username);
        mPassword = (EditText) mDialogView.findViewById(R.id.ftp_password);

        mError = (TextView) mDialogView.findViewById(R.id.error);

        mAnonymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUserName.setEnabled(!isChecked);
                mPassword.setEnabled(!isChecked);
            }
        });

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

        if (isNullOrEmpty(mServer.getText().toString())) {
            setErrorMessage(App.sInstance.getString(R.string.error_empty_server));
            return false;
        }

        int port = tryParse(mPort.getText().toString(), -1);

        if (port < 0 || port > 65535) {
            setErrorMessage(App.sInstance.getString(R.string.error_wrong_port));
            return false;
        }

        return true;
    }

    private void clearError() {
        updateErrorState("", View.GONE);
    }

    private void setErrorMessage(final String errorMessage) {
        updateErrorState(errorMessage, View.VISIBLE);
    }

    private void setLoading(final boolean isLoading) {
        Message.obtain(mHandler, new Runnable() {
            @Override
            public void run() {
                mDialogView.findViewById(R.id.auth_form).setVisibility(isLoading ? View.GONE : View.VISIBLE);
                mDialogView.findViewById(R.id.progress_form).setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        }).sendToTarget();

    }

    private void updateErrorState(final String errorMessage, final int visibility) {
        Message.obtain(mHandler, new Runnable() {
            @Override
            public void run() {
                mError.setVisibility(visibility);
                mError.setText(errorMessage);
            }
        }).sendToTarget();
    }

    private void connect() {
        setLoading(true);
        runAsynk(mConnectRunnable);
    }

    Runnable mConnectRunnable = new Runnable() {
        @Override
        public void run() {
            boolean anonymous = mAnonymous.isChecked();

            try {
                App.sInstance.getFtpApi().connectAndSave(mServer.getText().toString(), Extensions.tryParse(mPort.getText().toString(), 21),
                        mMode.getCheckedRadioButtonId() == R.id.ftp_mode_active,
                        anonymous ? "anonymous" : mUserName.getText().toString(),
                        anonymous ? "" : mPassword.getText().toString());
            } catch (InAppAuthException e) {
                setErrorMessage(e.getErrorMessage());
                setLoading(false);
                return;
            }

            dismiss();

            mHandler.sendEmptyMessage(FileSystemController.FTP_CONNECTED);
        }
    };
}
