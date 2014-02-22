package com.openfarmanager.android.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.LauncherAdapter;
import com.openfarmanager.android.adapters.NetworkEntryAdapter;
import com.openfarmanager.android.controllers.FileSystemController;
import com.openfarmanager.android.core.network.datasource.DataSource;
import com.openfarmanager.android.filesystem.FakeFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.FileActionEnum;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;
import com.openfarmanager.android.utils.Extensions;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static com.openfarmanager.android.controllers.FileSystemController.EXIT_FROM_NETWORK_STORAGE;

/**
 * Created by sergii on 11/10/2013.
 */
public class GenericPanel extends MainPanel {

    public static final int START_LOADING = 10000;
    public static final int STOP_LOADING = 10001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupHandler();
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mFileSystemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((LauncherAdapter) adapterView.getAdapter()).onItemClick(i);
            }
        });


        mFileSystemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                LauncherAdapter adapter = (LauncherAdapter) adapterView.getAdapter();
                FileProxy file = adapter.getItem(i);
                mSelectedFiles.clear();
                mSelectedFiles.add(file);
                openFileActionMenu();
                return true;
            }
        });

        mCurrentPathView.setText(getString(R.string.applications));
        mCurrentPathView.setOnLongClickListener(null);

        mFileSystemList.setAdapter(new LauncherAdapter(mAdapterHandler));
        return view;
    }

    public boolean isFileSystemPanel() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFileSystemList.getAdapter() != null) {
            ((LauncherAdapter) mFileSystemList.getAdapter()).refresh();
        }
    }

    protected FileActionEnum[] getAvailableActions() {
        return ((LauncherAdapter)mFileSystemList.getAdapter()).getAvailableActions();
    }


    @Override
    public void executeAction(FileActionEnum action, MainPanel inactivePanel) {
        LauncherAdapter adapter = (LauncherAdapter) mFileSystemList.getAdapter();
        adapter.setSelectedFiles(mSelectedFiles);
        adapter.executeAction(action, inactivePanel);
    }

    public void navigateParent() {
        mHandler.sendMessage(mHandler.obtainMessage(FileSystemController.EXIT_FROM_GENERIC_PANEL, mPanelLocation));
    }

    public boolean isRootDirectory() {
        return true;
    }

    protected boolean isCopyFolderSupported() {
        return false;
    }

    public boolean isSearchSupported() {
        return false;
    }

    private Handler mAdapterHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_LOADING:
                    setIsLoading(true);
                    break;
                case STOP_LOADING:
                    setIsLoading(false);
                    break;
            }
        }
    };
}
