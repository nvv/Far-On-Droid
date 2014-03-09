package com.openfarmanager.android.googledrive;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;

import com.openfarmanager.android.googledrive.api.Api;
import com.openfarmanager.android.googledrive.model.About;
import com.openfarmanager.android.googledrive.model.Token;
import com.openfarmanager.android.googledrive.model.exceptions.TokenExpiredException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.openfarmanager.android.googledrive.GoogleDriveConstants.*;

/**
 * author: Vlad Namashko
 */
public class GoogleDriveAuthWindow extends PopupWindow {

    public static final int MSG_SHOW_LOADING_DIALOG = 10000;
    public static final int MSG_HIDE_LOADING_DIALOG = 10001;

    public static final int MSG_ARG_SUCCESS = 100000;
    public static final int MSG_ARG_ERROR = 100001;

    public GoogleDriveAuthWindow(Activity context, final Handler handler) {
        super(context);
        final View view = context.getLayoutInflater().inflate(R.layout.dialog_google_drive_auth, null);
        setContentView(view);
        setFocusable(true);
        setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        setHeight(ViewGroup.LayoutParams.FILL_PARENT);

        WebView webView = (WebView) view.findViewById(R.id.web_view_auth);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {

                if (url.contains("code=")) {
                    handler.sendEmptyMessage(MSG_SHOW_LOADING_DIALOG);


                    ThreadPool.sInstance.runAsynk(new Runnable() {
                        @Override
                        public void run() {
                            Message message = handler.obtainMessage(MSG_HIDE_LOADING_DIALOG);
                            try {
                                Token token = Api.sInstance.getAuthToken(url);
                                message.obj = new Pair<About, Token>(Api.sInstance.getAbout(token), token) ;
                                message.arg1 = MSG_ARG_SUCCESS;
                            } catch (TokenExpiredException e) {

                            } catch (Exception e) {
                                message.arg1 = MSG_ARG_ERROR;
                            }

                            handler.sendMessage(message);
                        }
                    });


                    dismiss();
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        webView.loadUrl(Api.sInstance.getAuthCodeUrl());

    }

}
