package com.openfarmanager.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.openfarmanager.android.utils.Extensions.getResourceId;

public class Help extends Activity {

    private static final String ASSET = "file:///android_asset/";
    private WebView mWebview;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.help);

        mWebview = (WebView) findViewById(R.id.help_web_view);

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(ASSET + "far_help/")) {
                    int resourceId = getResourceId("raw", url.substring((ASSET + "far_help/").length()));
                    if (resourceId != 0) {
                        mWebview.loadDataWithBaseURL(ASSET, readTextFromResource(resourceId), "text/html", "utf-8", null);
                    }
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        mWebview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_HOME: case KeyEvent.KEYCODE_MOVE_HOME:
                        mWebview.loadDataWithBaseURL(ASSET, readTextFromResource(R.raw.help), "text/html", "utf-8", null);
                        return true;
                    case KeyEvent.KEYCODE_ESCAPE:
                        finish();
                        return true;
                }

                return false;
            }
        });

        mWebview.loadDataWithBaseURL(ASSET, readTextFromResource(R.raw.help), "text/html", "utf-8", null);
    }

    private String readTextFromResource(int resourceID) {
        InputStream raw = getResources().openRawResource(resourceID);

        BufferedReader br = new BufferedReader(new InputStreamReader(raw));
        StringBuilder builder = new StringBuilder();
        String line;

        try {
            while ((line = br.readLine()) != null)   {
                builder.append(line);
            }
            raw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

}