package com.openfarmanager.android.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;

import com.bitcasa.client.BitcasaClient;
import com.openfarmanager.android.R;

/**
 * author: Vlad Namashko
 */
public class BitcasaLoginDialog extends PopupWindow {

    private WebView mWebview;
    public static final String EXTRA_BITCASA_AUTH_URL = "extra_bitcasa_authentication_url";
    public static final String EXTRA_BITCASA_AUTH_CODE = "extra_bitcasa_authentication_code";
    public static final int REQUEST_CODE_BITCASA_AUTH = 0;
    public static final int RESULT_CODE_BITCASA_AUTH = 1;

    final static private String CLIENT_ID = "280e9b83";
    final static private String CLIENT_SECRET = "a997bc4dc1bc41decb22b43950b16678";

    public BitcasaLoginDialog(Activity context, final Handler handler) {
        super(context);
        final View view = context.getLayoutInflater().inflate(R.layout.dialog_bitcasa_auth, null);
        setContentView(view);
        mWebview = (WebView) view.findViewById(com.openfarmanager.android.googledrive.R.id.web_view_auth);
        setFocusable(true);
        setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        setHeight(ViewGroup.LayoutParams.FILL_PARENT);

//        WebSettings s = mWebview.getSettings();
//        s.setLoadWithOverviewMode(true);
//        s.setLoadsImagesAutomatically(true);
//        s.setUseWideViewPort(true);
//        s.setJavaScriptEnabled(true);
//        s.setSupportZoom(true);
//        s.setBuiltInZoomControls(true);

        mWebview.getSettings().setJavaScriptEnabled(true);
        BitcasaClient mBitcasaClient = new BitcasaClient(CLIENT_ID, CLIENT_SECRET);
        String authorizationUrl = mBitcasaClient.getAuthorizationUrl(CLIENT_ID);

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                String authorization_code = null;

                System.out.println(":::::::::ыыы  " + url);

                if (url.contains("authorization_code=")) {
                    String[] token = url.split("authorization_code=");
                    authorization_code = token[1];

                    Intent data = new Intent();
                    data.putExtra(EXTRA_BITCASA_AUTH_CODE, authorization_code);
//                    BitcasaLoginActivity.this.setResult(RESULT_CODE_BITCASA_AUTH, data);
//                    finish();

                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        mWebview.loadUrl(authorizationUrl);
    }

}
