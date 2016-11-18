package com.openfarmanager.android;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.UriPermission;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.openfarmanager.android.fragments.BaseFileSystemPanel;
import com.openfarmanager.android.fragments.MainToolbarPanel;
import com.openfarmanager.android.fragments.RequestPermissionFragment;
import com.openfarmanager.android.fragments.YesNoDialog;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.tips.MainTips;
import com.openfarmanager.android.toolbar.MenuBuilder;
import com.openfarmanager.android.toolbar.MenuItemImpl;
import com.openfarmanager.android.view.ToastNotification;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
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
    public static String RESULT_REQUEST_SDCARD_ACCEESS = "RESULT_REQUEST_SDCARD_ACCEESS";

    private FileSystemController mFileSystemController;
    protected CompositeSubscription mSubscription;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BaseFileSystemPanel.REQUEST_CODE_REQUEST_PERMISSION && Build.VERSION.SDK_INT >= 21 && data != null) {
            getContentResolver().takePersistableUriPermission(data.getData(),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            return;
        }

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

        if (data != null && data.getBooleanExtra(RESULT_REQUEST_SDCARD_ACCEESS, false)) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, BaseFileSystemPanel.REQUEST_CODE_REQUEST_PERMISSION);
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

        if (askPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            detectExternalStorages(new Runnable() {
                @Override
                public void run() {
                    showTips();
                }
            });
        }
        if (isHardwareKeyboardAvailable()) {
            ToastNotification.makeText(App.sInstance.getApplicationContext(), getString(R.string.hardware_keyboard), Toast.LENGTH_LONG).show();
        }

        setupToolbarVisibility();
    }

    @TargetApi(21)
    private void detectExternalStorages(final Runnable callback) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            callback.run();
            return;
        }
        try {
            File[] dirs = getExternalFilesDirs(null);
            for (File dir : dirs) {
                dir = dir.getParentFile().getParentFile().getParentFile().getParentFile();
                if (Environment.isExternalStorageRemovable(dir) && !App.sInstance.getSettings().isSDCardPermissionAsked()) {
                    List<UriPermission> persistedUriPermissions = App.sInstance.getContentResolver().getPersistedUriPermissions();
                    if (persistedUriPermissions.size() == 0 || !persistedUriPermissions.get(0).isWritePermission()) {
                        try {
                            RequestPermissionFragment.newInstance(getString(R.string.sd_card_detected), new YesNoDialog.YesNoDialogListener() {
                                @Override
                                public void yes() {
                                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                                    startActivityForResult(intent, BaseFileSystemPanel.REQUEST_CODE_REQUEST_PERMISSION);
                                }

                                @Override
                                public void no() {
                                    App.sInstance.getSettings().setSDCardPermissionAsked(true);
                                    callback.run();
                                }
                            }).show(getSupportFragmentManager(), "errorDialog");
                            return;
                        } catch (Exception ignore) {
                        }
                    }
                }
            }
            callback.run();
        } catch (Exception ignored) {
            callback.run();
        }
    }

    protected void onPermissionsResult(Map<String, Integer> permissions) {
        showTips();
    }

    private boolean isHardwareKeyboardAvailable() {
        return (getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS);
    }

    private void showTips() {
        mFileSystemController.invalidate();

        Settings settings = App.sInstance.getSettings();
        if (settings.isShowTips()) {
            new MainTips(this, mFileSystemController, (MainToolbarPanel) getSupportFragmentManager().findFragmentById(R.id.toolbar));
            settings.getSharedPreferences().edit().putBoolean(Settings.SHOW_TIPS, false).apply();
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
        if (mFileSystemController.onKeyDown(keyCode, event))
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
