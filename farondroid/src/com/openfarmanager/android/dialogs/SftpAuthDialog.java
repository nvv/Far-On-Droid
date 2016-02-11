package com.openfarmanager.android.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.controllers.FileSystemController;
import com.openfarmanager.android.model.exeptions.InAppAuthException;
import com.openfarmanager.android.utils.Extensions;

/**
 * @author Vlad Namashko
 */
public class SftpAuthDialog extends FtpAuthDialog {

    public SftpAuthDialog(Context context, Handler handler) {
        super(context, handler);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogView = View.inflate(App.sInstance.getApplicationContext(), R.layout.dialog_sftp_authentication, null);

        mServer = (EditText) mDialogView.findViewById(R.id.ftp_server);
        mPort = (EditText) mDialogView.findViewById(R.id.ftp_port);
        mUserName = (EditText) mDialogView.findViewById(R.id.ftp_username);
        mPassword = (EditText) mDialogView.findViewById(R.id.ftp_password);
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

    @Override
    protected Runnable getConnectRunnable() {
        return mConnectRunnable;
    }

    Runnable mConnectRunnable = new Runnable() {
        @Override
        public void run() {

            try {
                App.sInstance.getSftpApi().connectAndSave(mServer.getText().toString(), Extensions.tryParse(mPort.getText().toString(), 21),
                        mUserName.getText().toString(), mPassword.getText().toString(), false, null);
            } catch (InAppAuthException e) {
                setErrorMessage(e.getErrorMessage());
                setLoading(false);
                return;
            }

            dismiss();

            mHandler.sendEmptyMessage(FileSystemController.SFTP_CONNECTED);
        }
    };
}
