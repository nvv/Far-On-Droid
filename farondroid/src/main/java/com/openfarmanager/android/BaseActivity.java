package com.openfarmanager.android;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.openfarmanager.android.fragments.MainToolbarPanel;
import com.openfarmanager.android.tips.HideToolbarTips;
import com.openfarmanager.android.toolbar.MenuItemImpl;
import com.openfarmanager.android.dialogs.QuickPopupDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * @author Vlad Namashko.
 */
public abstract class BaseActivity extends FragmentActivity {

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1024;

    protected QuickPopupDialog mMenuPopup;

    protected void setupToolbarVisibility() {
        if (App.sInstance.getSettings().isHideMainToolbar()) {
            hideToolbar();

            final View view = findViewById(android.R.id.content);
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @SuppressLint("NewApi")
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int offset = (int) (10 * getResources().getDisplayMetrics().density);
                    if (mMenuPopup == null) {
                        mMenuPopup = new QuickPopupDialog(BaseActivity.this, view, R.layout.quick_action_menu_popup);
                        mMenuPopup.setPosition(Gravity.LEFT | Gravity.BOTTOM, offset);

                        mMenuPopup.getContentView().findViewById(R.id.quick_action_menu).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                MainToolbarPanel.SubMenuDialog dialog = MainToolbarPanel.SubMenuDialog.newInstance(
                                        getItems(), new MainToolbarPanel.SubMenuDialog.OnActionSelectedListener() {
                                            @Override
                                            public void onActionSelected(MenuItem item) {
                                                onToolbarItemSelected(item);
                                            }
                                        });
                                try {
                                    dialog.show(BaseActivity.this.getSupportFragmentManager(), "dialog");
                                } catch (Exception ignore) {
                                }
                            }
                        });
                    }

                    mMenuPopup.show();

                    new HideToolbarTips(BaseActivity.this, offset);

                }
            });
        } else {
            showToolbar();
            if (mMenuPopup != null) {
                mMenuPopup.dismiss();
            }
        }
    }

    protected boolean askPermission(String[] permissions) {

        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        final List<String> permissionsRequested = new ArrayList<>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                continue;
            }
            permissionsRequested.add(permission);
        }

        if (!permissionsRequested.isEmpty()) {
            requestPermissions(permissionsRequested.toArray(new String[permissionsRequested.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }

    protected void onPermissionsResult(Map<String, Integer> permissions) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> hash = new HashMap<>();
                for (int i = 0; i < permissions.length; i++) {
                    hash.put(permissions[i], grantResults[i]);
                }
                onPermissionsResult(hash);
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected abstract void hideToolbar();

    protected abstract void showToolbar();

    protected abstract Handler getHandler();

    protected abstract ArrayList<MenuItemImpl> getItems();

    protected abstract void onToolbarItemSelected(MenuItem item);
}
