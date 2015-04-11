package com.openfarmanager.android;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.widget.Toast;
import com.openfarmanager.android.controllers.EditViewController;
import com.openfarmanager.android.fragments.Viewer;
import com.openfarmanager.android.fragments.ViewerToolbar;
import com.openfarmanager.android.toolbar.MenuItemImpl;
import com.openfarmanager.android.view.ToastNotification;

import java.io.File;
import java.util.ArrayList;

import static com.openfarmanager.android.controllers.EditViewController.*;

/**
 * File viewer activity
 */
public class FileView extends BaseActivity {

    private EditViewController mController;
    private Viewer mViewer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity);
        FragmentManager manager = getSupportFragmentManager();
        mViewer = (Viewer) manager.findFragmentById(R.id.viewer);
        mController = new EditViewController(mViewer, (ViewerToolbar) manager.findFragmentById(R.id.toolbar));

        try {
            mViewer.openFile(new File(getIntent().getData().getPath()));
        } catch (Exception e) { // possible NPE Exception
            ToastNotification.makeText(App.sInstance.getApplicationContext(),
                    App.sInstance.getString((R.string.error_open_file)), Toast.LENGTH_SHORT).show();
            finish();
        }

        setupToolbarVisibility();
    }

    @Override
    protected void hideToolbar() {
        mController.hideToolbar();
    }

    @Override
    protected void showToolbar() {
        mController.showToolbar();
    }

    @Override
    protected Handler getHandler() {
        return mController.getToolbarHandler();
    }

    @Override
    protected ArrayList<MenuItemImpl> getItems() {

        ArrayList<MenuItemImpl> menuItems = new ArrayList<>();

        if (!mViewer.isFileTooBig()) {
            menuItems.add(new MenuItemImpl(null, SAVE, R.id.save, 0, 0, getString(R.string.btn_save), 0));
            menuItems.add(new MenuItemImpl(null, EDIT, R.id.edit, 0, 0, getString(R.string.btn_edit), 0));
        }
        menuItems.add(new MenuItemImpl(null, SEARCH, R.id.search, 0, 0, getString(R.string.search), 0));
        if (!mViewer.isFileTooBig()) {
            menuItems.add(new MenuItemImpl(null, REPLACE, R.id.replace, 0, 0, getString(R.string.replace_to), 0));
        }
        menuItems.add(new MenuItemImpl(null, GOTO, R.id.go_to, 0, 0, getString(R.string.go_to), 0));
        menuItems.add(new MenuItemImpl(null, ENCODING, R.id.encoding, 0, 0, getString(R.string.btn_encoding), 0));

        return menuItems;
    }

    @Override
    protected void onToolbarItemSelected(MenuItem item) {
        getHandler().sendEmptyMessage(item.getGroupId());
    }
}