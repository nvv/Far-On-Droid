package com.openfarmanager.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;
import com.openfarmanager.android.controllers.EditViewController;
import com.openfarmanager.android.fragments.Viewer;
import com.openfarmanager.android.fragments.ViewerToolbar;
import com.openfarmanager.android.view.ToastNotification;

import java.io.File;

/**
 * File viewer activity
 */
public class FileView extends FragmentActivity {

    private EditViewController mController;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity);
        FragmentManager manager = getSupportFragmentManager();
        mController = new EditViewController((Viewer) manager.findFragmentById(R.id.viewer),
                (ViewerToolbar) manager.findFragmentById(R.id.toolbar));
        try {
            ((Viewer) manager.findFragmentById(R.id.viewer)).openFile(new File(getIntent().getData().getPath()));
        } catch (Exception e) { // possible NPE Exception
            ToastNotification.makeText(App.sInstance.getApplicationContext(),
                    App.sInstance.getString((R.string.error_open_file)), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}