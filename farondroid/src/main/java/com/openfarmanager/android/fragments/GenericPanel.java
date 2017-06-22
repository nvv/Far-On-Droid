package com.openfarmanager.android.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.FileSystemAdapter;
import com.openfarmanager.android.adapters.LauncherAdapter;
import com.openfarmanager.android.controllers.FileSystemController;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.FileActionEnum;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created on 11/10/2013.
 *
 * @author Sergey O
 */
public class GenericPanel extends MainPanel {

    public static final int START_LOADING = 10000;
    public static final int STOP_LOADING = 10001;

    private CompositeDisposable mSubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupHandler();
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mSubscription = new CompositeDisposable();

        mFileSystemList.setOnItemClickListener(new FileSystemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LauncherAdapter adapter = (LauncherAdapter) mFileSystemList.getAdapter();

                if (mIsMultiSelectMode) {
                    updateLongClick(position, adapter, false);
                } else {
                    adapter.onItemClick(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                LauncherAdapter adapter = (LauncherAdapter) mFileSystemList.getAdapter();
                updateLongClick(position, adapter, true);
                openFileActionMenu();
            }
        });

        mActionBar.updateCurrentPath(getString(R.string.applications));

        mFileSystemList.initAdapter(new LauncherAdapter(mAdapterHandler, mSubscription));
        return view;
    }

    @Override
    protected void setNavigationButtonsVisibility(final boolean forceHide) {
        mActionBar.updateNavigationItemsVisibility(true, false, false);
    }

    private void updateLongClick(int i, LauncherAdapter adapter, boolean longClick) {

        FileProxy fileProxy = adapter.getItem(i);

        if (mSelectedFiles.contains(fileProxy) && !longClick) {
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
        mSubscription.clear();
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
        mHandler.sendMessage(mHandler.obtainMessage(FileSystemController.EXIT_FROM_GENERIC_PANEL, getPanelLocation()));
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

    protected boolean isBookmarksSupported() {
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
