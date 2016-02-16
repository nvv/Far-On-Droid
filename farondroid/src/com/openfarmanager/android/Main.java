package com.openfarmanager.android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.dropbox.client2.exception.DropboxException;
import com.openfarmanager.android.controllers.FileSystemController;
import com.openfarmanager.android.controllers.FileSystemControllerSmartphone;
import com.openfarmanager.android.core.Settings;
import com.openfarmanager.android.core.network.NetworkConnectionManager;
import com.openfarmanager.android.core.network.dropbox.DropboxAPI;
import com.openfarmanager.android.fragments.MainToolbarPanel;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.tips.MainTips;
import com.openfarmanager.android.toolbar.MenuBuilder;
import com.openfarmanager.android.toolbar.MenuItemImpl;
import com.openfarmanager.android.utils.Extensions;
import com.openfarmanager.android.view.ToastNotification;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class Main extends BaseActivity {

    private static String TAG = "MainFragmentActivity";

    public static int RESULT_SETTINGS_CHANGED = 100;
    public static String RESULT_CODE_PANELS_MODE_CHANGED = "RESULT_CODE_PANELS_MODE_CHANGED";
    public static String RESULT_BOTTOM_PANEL_INVALIDATE = "RESULT_BOTTOM_PANEL_INVALIDATE";
    public static String RESULT_SHOW_HINT = "RESULT_SHOW_HINT";

    private FileSystemController mFileSystemController;
    protected CompositeSubscription mSubscription;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getBooleanExtra(RESULT_CODE_PANELS_MODE_CHANGED, false)) {
            Intent intent = getIntent();
            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();

            overridePendingTransition(0, 0);
            startActivity(intent);
        }

        if (data != null && data.getBooleanExtra(RESULT_BOTTOM_PANEL_INVALIDATE, false)) {
            mFileSystemController.invalidateToolbar();
        }

        if (data != null && data.getBooleanExtra(RESULT_SHOW_HINT, false)) {
            showTips();
        }

        setupToolbarVisibility();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscription = new CompositeSubscription();
        App.sInstance.getNetworkConnectionManager().setRxSubscription(mSubscription);
        setContentView(App.sInstance.getSettings().isMultiPanelMode() ? R.layout.main_two_panels : R.layout.main_one_panel);
        if (findViewById(R.id.view_pager) == null) {
            mFileSystemController = new FileSystemController(getSupportFragmentManager(), findViewById(R.id.root_view));
        } else {
            mFileSystemController = new FileSystemControllerSmartphone(getSupportFragmentManager(), findViewById(R.id.root_view));
        }
        App.sInstance.setFileSystemController(mFileSystemController);

        mFileSystemController.restorePanelState();

        if (askPermission(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE })) {
            showTips();
        }

        if (getIntent() != null && getIntent().getData() != null) {
            onLogin();
        }

        if (isHardwareKeyboardAvailable()) {
            ToastNotification.makeText(App.sInstance.getApplicationContext(), getString(R.string.hardware_keyboard), Toast.LENGTH_LONG).show();
        }

        setupToolbarVisibility();
    }

    protected void onPermissionsResult(Map<String, Integer> permissions) {
        showTips();
    }

    private boolean isHardwareKeyboardAvailable() {
        return (getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS);
    }

    private void onLogin () {
        Uri data = getIntent().getData();
        setIntent(null);
        Pattern pattern = Pattern.compile("access_token=(.*?)(&|$)");
        Matcher matcher = pattern.matcher(data.toString());
        if (matcher.find()) {
            final String token = matcher.group(1);
            if (!TextUtils.isEmpty(token)) {
                App.sInstance.getNetworkConnectionManager().yandexDiskTokenReceived(this, token, mFileSystemController.getActivePanel());
            } else {
                Log.w(TAG, "YandexDisk onRegistrationSuccess: empty token");
            }
        } else {
            Log.w(TAG, "YandexDisk onRegistrationSuccess: token not found in return url");
        }
    }

    private void showTips() {
        Settings settings = App.sInstance.getSettings();
        if (settings.isShowTips()) {
            new MainTips(this, mFileSystemController, (MainToolbarPanel) getSupportFragmentManager().findFragmentById(R.id.toolbar));
            settings.getSharedPreferences().edit().putBoolean(Settings.SHOW_TIPS, false).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        (findViewById(R.id.panels_holder)).setBackgroundColor(App.sInstance.getSettings().getMainPanelColor());

        final DropboxAPI dropboxAPI = App.sInstance.getDropboxApi();
        if (dropboxAPI == null) {
            return;
        }
        NetworkConnectionManager manager = App.sInstance.getNetworkConnectionManager();
        if (dropboxAPI.getSession().authenticationSuccessful() && manager.isNetworkAuthRequested()) {
            dropboxAPI.getSession().finishAuthentication();
            manager.resetNetworkAuth();

            mFileSystemController.showProgressDialog(R.string.loading);
            Subscription subscription = Observable.create(new Observable.OnSubscribe<Void>() {
                @Override
                public void call(Subscriber<? super Void> subscriber) {
                    try {
                        com.dropbox.client2.DropboxAPI.Account account = dropboxAPI.accountInfo();
                        String userName = account.displayName + "(" + account.uid + ")";
                        dropboxAPI.storeAccessTokens(userName, dropboxAPI.getSession().getAccessTokenPair());
                    } catch (DropboxException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(null);
                }
            }).subscribeOn(Schedulers.computation()).
                    observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Void>() {
                @Override
                public void call(Void aVoid) {
                    mFileSystemController.dismissProgressDialog();
                    mFileSystemController.openNetworkPanel(NetworkEnum.Dropbox);
                }
            });
            mSubscription.add(subscription);
        }
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            mFileSystemController.savePanelState();
        } catch (IllegalStateException ignore) {
            //something very unexpected, but there is a lot of crashes...
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mFileSystemController.onKeyDown(keyCode, event))
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void hideToolbar() {
        mFileSystemController.hideMainToolbar();
    }

    @Override
    protected void showToolbar() {
        mFileSystemController.showMainToolbar();
    }

    @Override
    protected Handler getHandler() {
        return mFileSystemController.getToolbarHandler();
    }

    @Override
    protected ArrayList<MenuItemImpl> getItems() {
        Menu menu = new MenuBuilder(this);
        int res = getResources().getIdentifier("main", "menu", getPackageName());
        new MenuInflater(this).inflate(res, menu);
        return ((MenuBuilder) menu).getAllActionItems();
    }

    @Override
    protected void onToolbarItemSelected(MenuItem item) {
        getHandler().sendEmptyMessage(MainToolbarPanel.sActions.get(item.getItemId()));
    }
}
