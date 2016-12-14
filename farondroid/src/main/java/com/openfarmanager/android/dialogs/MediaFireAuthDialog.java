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
import com.openfarmanager.android.core.network.mediafire.MediaFireApi;
import com.openfarmanager.android.utils.Extensions;

import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;

/**
 * author: Vlad Namashko
 */
public class MediaFireAuthDialog extends Dialog {

    private Handler mHandler;
    private View mDialogView;
    private TextView mError;
    private EditText mUserName;
    private EditText mPassword;

    public MediaFireAuthDialog(Context context, Handler handler) {
        super(context, R.style.Action_Dialog);
        mHandler = handler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogView = View.inflate(App.sInstance.getApplicationContext(), R.layout.dialog_mediafire_authentication, null);

        mUserName = (EditText) mDialogView.findViewById(R.id.mediafire_username);
        mPassword = (EditText) mDialogView.findViewById(R.id.mediafire_password);

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
        Extensions.runAsync(mConnectRunnable);
    }

    private Runnable mConnectRunnable = new Runnable() {
        @Override
        public void run() {

            try {
                MediaFireApi api = App.sInstance.getMediaFireApi();
                api.startNewSession(mUserName.getText().toString(), mPassword.getText().toString());

//                MediaFire mf = new MediaFire(MediaFireApi.APP_ID, MediaFireApi.APP_KEY);
//                mf.startSessionWithEmail("VNamashko@gmail.com", "tubooR1r", null);
//                mf.startSessionWithEmail(mUserName.getText().toString(), mPassword.getText().toString(), null);
//                LinkedHashMap<String, Object> requestParams = new LinkedHashMap<String, Object>();
//                requestParams.put("response_format", "json");
//                requestParams.put("content_type", "files");
//                requestParams.put("chunk_size", 150);
//                FolderGetContentsResponse response = FolderApi.getContent(mf, requestParams, "1.3", FolderGetContentsResponse.class);
            } catch (Exception e) {
                setLoading(false);
                setErrorMessage(App.sInstance.getString(R.string.error_smb_wrong_credentials));
                return;
            }

            dismiss();

            mHandler.sendEmptyMessage(FileSystemController.MEDIA_FIRE_CONNECTED);
        }
    };
}
