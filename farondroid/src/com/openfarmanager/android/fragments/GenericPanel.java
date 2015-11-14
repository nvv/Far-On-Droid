package com.openfarmanager.android.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.LauncherAdapter;
import com.openfarmanager.android.controllers.FileSystemController;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.FileActionEnum;

import rx.subscriptions.CompositeSubscription;

/**
 * Created on 11/10/2013.
 *
 * @author Sergey O
 */
public class GenericPanel extends MainPanel {

    public static final int START_LOADING = 10000;
    public static final int STOP_LOADING = 10001;

    private CompositeSubscription mSubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupHandler();
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mSubscription = new CompositeSubscription();
        mFileSystemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                LauncherAdapter adapter = (LauncherAdapter) adapterView.getAdapter();

                if (mIsMultiSelectMode) {
                    updateLongClick(i, adapter, false);
                } else {
                    adapter.onItemClick(i);
                }
            }
        });


        mFileSystemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                LauncherAdapter adapter = (LauncherAdapter) adapterView.getAdapter();
                updateLongClick(i, adapter, true);
                openFileActionMenu();
                return true;
            }
        });

        mCurrentPathView.setText(getString(R.string.applications));
        mCurrentPathView.setOnLongClickListener(null);

        mFileSystemList.setAdapter(new LauncherAdapter(mAdapterHandler, mSubscription));
        return view;
    }

    private void updateLongClick(int i, LauncherAdapter adapter, boolean longClick) {

        FileProxy fileProxy = adapter.getItem(i);

        if (mSelectedFiles.contains(fileProxy)) {
            mSelectedFiles.remove(fileProxy);
        } else {
            if (mSelectedFiles.contains(fileProxy)) {
                return;
            }
            mSelectedFiles.add(0, fileProxy);
        }

        adapter.setSelectedFiles(mSelectedFiles);
        adapter.notifyDataSetChanged();
    }

    public boolean isFileSystemPanel() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        LauncherAdapter adapter = (LauncherAdapter) mFileSystemList.getAdapter();
        if (adapter != null) {
            mSelectedFiles.clear();
            adapter.refresh();
        }
    }

    @Override
    public void onDetach () {
        super.onDetach();
        mSubscription.unsubscribe();
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
