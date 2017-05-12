package com.openfarmanager.android.googledrive;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.openfarmanager.android.googledrive.api.Api;
import com.openfarmanager.android.googledrive.model.About;
import com.openfarmanager.android.googledrive.model.Token;
import com.openfarmanager.android.googledrive.model.exceptions.TokenExpiredException;

/**
 * author: Vlad Namashko
 */
public class GoogleDriveAuthWindow extends Dialog {

    public static final int MSG_SHOW_LOADING_DIALOG = 10000;
    public static final int MSG_HIDE_LOADING_DIALOG = 10001;

    public static final int MSG_ARG_SUCCESS = 100000;
    public static final int MSG_ARG_ERROR = 100001;

    private Handler mHandler;
    private View mView;

    public GoogleDriveAuthWindow(Activity context, final Handler handler) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mView = context.getLayoutInflater().inflate(R.layout.dialog_google_drive_auth, null);
        mHandler = handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(mView);

        final Api api = new Api();

        final WebView webView = (WebView) mView.findViewById(R.id.web_view_auth);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) AppleWebKit/<WebKit Rev> (KHTML, like Gecko) Chrome/<Chrome Rev> Mobile Safari/<WebKit Rev>");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {

                if (url.contains("code=")) {
                    mHandler.sendEmptyMessage(MSG_SHOW_LOADING_DIALOG);

                    ThreadPool.sInstance.runAsynk(new Runnable() {
                        @Override
                        public void run() {
                            Message message = mHandler.obtainMessage(MSG_HIDE_LOADING_DIALOG);
                            try {
                                Token token = api.getAuthToken(url);
                                message.obj = new Pair<About, Token>(api.getAbout(token), token) ;
                                message.arg1 = MSG_ARG_SUCCESS;
                            } catch (TokenExpiredException e) {

                            } catch (Exception e) {
                                message.arg1 = MSG_ARG_ERROR;
                            }

                            mHandler.sendMessage(message);
                        }
                    });


                    dismiss();
                } else if (url.contains("error=access_denied")) {
                    webView.loadUrl(api.getAuthCodeUrl());
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        webView.loadUrl(api.getAuthCodeUrl());

    }
}
