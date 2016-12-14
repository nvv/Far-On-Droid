package com.openfarmanager.android.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.controllers.FileSystemController;
import com.openfarmanager.android.model.exeptions.InAppAuthException;

/**
 * @author Vlad Namashko
 */
public class WebDavAuthDialog extends SmbAuthDialog {

    public WebDavAuthDialog(Context context, Handler handler) {
        super(context, handler, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScanNetwork.setVisibility(View.GONE);
        ((TextView) mDialogView.findViewById(R.id.domain_label)).setText(R.string.server);
        ((TextView) mDialogView.findViewById(R.id.current_action)).setText(R.string.webdav_server);
    }

    @Override
    protected int getEmptyServerError() {
        return R.string.error_empty_server;
    }

    @Override
    protected Runnable getConnectRunnable() {
        return mConnectRunnable;
    }

    Runnable mConnectRunnable = new Runnable() {
        @Override
        public void run() {

            try {
                App.sInstance.getWebDavApi().connectAndSave(mDomain.getText().toString(),
                        mUserName.getText().toString(), mPassword.getText().toString());
            } catch (InAppAuthException e) {
                setErrorMessage(e.getErrorMessage());
                setLoading(false);
                return;
            }

            dismiss();

            mHandler.sendEmptyMessage(FileSystemController.WEBDAV_CONNECTED);
        }
    };

}
