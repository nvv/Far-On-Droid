package com.openfarmanager.android.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;

import com.bitcasa.client.BitcasaClient;
import com.bitcasa.client.datamodel.AccountInfo;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.network.bitcasa.BitcasaApi;
import com.openfarmanager.android.googledrive.GoogleDriveAuthWindow;
import com.openfarmanager.android.googledrive.ThreadPool;
import com.openfarmanager.android.googledrive.model.Token;

/**
 * author: Vlad Namashko
 */
public class BitcasaLoginDialog extends Dialog {

    public static final int MSG_SHOW_LOADING_DIALOG = 20000;
    public static final int MSG_HIDE_LOADING_DIALOG = 20001;
    public static final int MSG_RESTORE_SUCCESS = 20002;

    public static final int MSG_ARG_SUCCESS = 200000;
    public static final int MSG_ARG_ERROR = 200001;

    private Handler mHandler;
    private View mView;

    public BitcasaLoginDialog(Activity context, final Handler handler) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mView = context.getLayoutInflater().inflate(R.layout.dialog_bitcasa_auth, null);
        mHandler = handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(mView);

        final BitcasaApi api = App.sInstance.getBitcasaApi();

        WebView webview = (WebView) mView.findViewById(R.id.web_view_auth);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.contains("authorization_code=")) {
                    mHandler.sendEmptyMessage(MSG_SHOW_LOADING_DIALOG);

                    String[] token = url.split("authorization_code=");
                    final String authorizationCode = token[1];

                    ThreadPool.sInstance.runAsynk(new Runnable() {
                        @Override
                        public void run() {
                            Message message = mHandler.obtainMessage(MSG_HIDE_LOADING_DIALOG);
                            try {
                                String token = api.getAccessToken(authorizationCode);
                                api.setup(token);
                                message.obj = new Pair<AccountInfo, String>(api.getAccountInfo(), token);
                                message.arg1 = MSG_ARG_SUCCESS;
                            } catch (Exception e) {
                                message.arg1 = MSG_ARG_ERROR;
                            }

                            mHandler.sendMessage(message);
                        }
                    });


                    dismiss();
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        webview.loadUrl(api.getAuthorizationUrl());
    }

}
