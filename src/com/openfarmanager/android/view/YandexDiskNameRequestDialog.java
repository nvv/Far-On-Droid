package com.openfarmanager.android.view;

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
import com.yandex.disk.client.Credentials;

import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;

/**
 * author: Vlad Namashko
 */
public class YandexDiskNameRequestDialog extends Dialog {

    private Handler mHandler;
    private View mDialogView;
    private TextView mError;
    private EditText mUserName;
    private String mToken;

    public YandexDiskNameRequestDialog(Context context, Handler handler, String token) {
        super(context, R.style.Action_Dialog);
        mHandler = handler;
        mToken = token;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogView = View.inflate(App.sInstance.getApplicationContext(), R.layout.dialog_yandex_username_request, null);

        mUserName = (EditText) mDialogView.findViewById(R.id.yandex_username);
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

                saveAccount();
            }
        });

        setContentView(mDialogView);
    }

    private boolean validate() {

        if (isNullOrEmpty(mUserName.getText().toString())) {
            setErrorMessage(App.sInstance.getString(R.string.error_yandex_account_empty));
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

    private void updateErrorState(final String errorMessage, final int visibility) {
        Message.obtain(mHandler, new Runnable() {
            @Override
            public void run() {
                mError.setVisibility(visibility);
                mError.setText(errorMessage);
            }
        }).sendToTarget();
    }

    private void saveAccount() {
        Message message = mHandler.obtainMessage(FileSystemController.YANDEX_DISK_USERNAME_RECEIVED);
        message.obj = new Credentials(mUserName.getText().toString(), mToken);
        mHandler.sendMessage(message);

        dismiss();
    }

}
