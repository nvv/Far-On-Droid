package com.openfarmanager.android;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.openfarmanager.android.fragments.MainToolbarPanel;
import com.openfarmanager.android.tips.HideToolbarTips;
import com.openfarmanager.android.toolbar.MenuItemImpl;
import com.openfarmanager.android.view.QuickPopupDialog;

import java.util.ArrayList;

/**
 * @author Vlad Namashko.
 */
public abstract class BaseActivity extends FragmentActivity {

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
                        mMenuPopup = new QuickPopupDialog(view, R.layout.quick_action_menu_popup);
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
                                } catch (Exception ignore) {}
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

    protected abstract void hideToolbar();

    protected abstract void showToolbar();

    protected abstract Handler getHandler();

    protected abstract ArrayList<MenuItemImpl> getItems();

    protected abstract void onToolbarItemSelected(MenuItem item);
}
