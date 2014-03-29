package com.openfarmanager.android.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.NetworkEntryAdapter;
import com.openfarmanager.android.core.network.datasource.DataSource;
import com.openfarmanager.android.core.network.datasource.DropboxDataSource;
import com.openfarmanager.android.core.network.datasource.FtpDataSource;
import com.openfarmanager.android.core.network.datasource.GoogleDriveDataSource;
import com.openfarmanager.android.core.network.datasource.SkyDriveDataSource;
import com.openfarmanager.android.core.network.datasource.SmbDataSource;
import com.openfarmanager.android.core.network.datasource.YandexDiskDataSource;
import com.openfarmanager.android.filesystem.FakeFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.FileActionEnum;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;
import com.openfarmanager.android.utils.Extensions;
import com.openfarmanager.android.view.ToastNotification;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;

import static com.openfarmanager.android.controllers.FileSystemController.EXIT_FROM_NETWORK_STORAGE;

/**
 * Panel for 'network filesystem' (like Dropbox, Google Drive etc).
 */
public class NetworkPanel extends MainPanel {

    private DataSource mDataSource;
    private OpenDirectoryTask mOpenDirectoryTask = new OpenDirectoryTask();
    private FileProxy mCurrentPath;

    protected FileProxy mLastSelectedFile;
    private NetworkAccount mCurrentNetworkAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupHandler();
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mFileSystemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                FileProxy file = (FileProxy) adapterView.getItemAtPosition(i);
                if (mIsMultiSelectMode) {
                    updateLongClickSelection(adapterView, file, false);
                    return;
                }
                mSelectedFiles.clear();

                if (i == 0) {
                    if (file.isRoot()) { // exit from network
                        exitFromNetwork();
                        return;
                    } else {
                        openDirectory(file.getParentPath());
                    }
                }

                if (file.isDirectory()) {
                    openDirectory(file.getFullPath());
                }

            }
        });

        mFileSystemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                return onLongClick(adapterView, i);
            }
        });

        setIsLoading(true);

        forceHideNavigationButtons();

        postInitialization();

        if (mDataSource == null) {
            ToastNotification.makeText(App.sInstance.getApplicationContext(),
                    getSafeString(R.string.error_unknown_unexpected_error), Toast.LENGTH_SHORT).show();
            exitFromNetwork();
        }

        mCurrentNetworkAccount = App.sInstance.getNetworkApi(getNetworkType()).getCurrentNetworkAccount();
        return view;
    }

    public NetworkAccount getCurrentNetworkAccount() {
        return mCurrentNetworkAccount;
    }

    public void setNetworkType(NetworkEnum networkType) {
        switch (networkType) {
            case Dropbox:
                mDataSource = new DropboxDataSource();
                break;
            case SkyDrive:
                mDataSource = new SkyDriveDataSource();
                break;
            case FTP:
                mDataSource = new FtpDataSource();
                break;
            case SMB:
                mDataSource = new SmbDataSource();
                break;
            case YandexDisk:
                mDataSource = new YandexDiskDataSource();
                break;
            case GoogleDrive:
                mDataSource = new GoogleDriveDataSource();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // selected files need to be updated after application resumes
        NetworkEntryAdapter adapter = (NetworkEntryAdapter) mFileSystemList.getAdapter();
        if (adapter != null) {
            adapter.setSelectedFiles(mSelectedFiles);
            adapter.notifyDataSetChanged();
        }

        setIsActivePanel(mIsActivePanel);
    }

    protected boolean onLongClick(AdapterView<?> adapterView, int i) {
        FileProxy file = (FileProxy) adapterView.getItemAtPosition(i);
        if (!file.isUpNavigator()) {
            mLastSelectedFile = file;
            updateLongClickSelection(adapterView, file, true);
            if (!file.isVirtualDirectory()) {
                openFileActionMenu();
            }
        }
        return true;
    }

    protected void updateLongClickSelection(AdapterView<?> adapterView, FileProxy file, boolean longClick) {
        if (file.isUpNavigator()) {
            return;
        }

        if (mSelectedFiles.contains(file) && !longClick) {
            mSelectedFiles.remove(file);
        } else {
            if (mSelectedFiles.contains(file)) {
                return;
            }
            mSelectedFiles.add(0, file);
        }

        NetworkEntryAdapter adapter = (NetworkEntryAdapter) adapterView.getAdapter();

        adapter.setSelectedFiles(mSelectedFiles);
        adapter.notifyDataSetChanged();
    }

    protected FileActionEnum[] getAvailableActions() {
        return FileActionEnum.getAvailableActionsForNetwork(mSelectedFiles, mLastSelectedFile);
    }

    public FileProxy getLastSelectedFile() {
        return mLastSelectedFile;
    }

    public void navigateParent() {
        if (mCurrentPath != null && !mCurrentPath.isRoot()) {
            openDirectory(mCurrentPath.getParentPath());
        } else {
            exitFromNetwork();
        }
    }

    public NetworkEnum getNetworkType() {
        return mDataSource.getNetworkTypeEnum();
    }

    public void exitFromNetwork() {
        if (mDataSource != null) {
            mDataSource.exitFromNetwork();
        }
        mHandler.sendMessage(mHandler.obtainMessage(EXIT_FROM_NETWORK_STORAGE, mPanelLocation));
    }

    public void openDirectory() {
        openDirectory("/");
    }

    public void openDirectoryAndSelect(String path, List<FileProxy> selectedFiles) {
        mPreSelectedFiles = selectedFiles;
        openDirectory(path);
    }

    public void openDirectory(final String path) {
        if (!mIsInitialized) {
            addToPendingList(new Runnable() {
                @Override
                public void run() {
                    openDirectory(path);
                }
            });
            return;
        }

        if (mOpenDirectoryTask != null) {
            mOpenDirectoryTask.cancel(true);
        }

        mOpenDirectoryTask = new OpenDirectoryTask();
        mOpenDirectoryTask.execute(path);
    }

    public void invalidate() {
        openDirectory(mDataSource.getParentPath(getCurrentPath()));
    }

    private void setCurrentPath(String path) {
        mCurrentPathView.setText(mDataSource.getNetworkType() + " : " + path);
    }

    public boolean isRootDirectory() {
        // definetely something going wrong so it's better to tell that this is root and exit from network to avoid crash.
        return mCurrentPath == null || mCurrentPath.isRoot();
    }

    public List<FileProxy> getFiles() {
        return mSelectedFiles;
    }

    public int getSelectedFilesCount() {
        return mSelectedFiles.size();
    }

    public boolean isFileSystemPanel() {
        return false;
    }

    protected boolean isCopyFolderSupported() {
        return false;
    }

    public boolean isSearchSupported() {
        return mDataSource.isSearchSupported();
    }

    public String getPanelType() {
        return mDataSource.getNetworkType();
    }

    public String getCurrentPath() {
        return mCurrentPath.getName();
    }

    protected void onNavigationItemSelected(int pos, List<String> items) {
        openDirectory(mDataSource.getParentPath(TextUtils.join("/", items.subList(0, pos + 1)).substring(1)));
    }

    public void select(String pattern, boolean inverseSelection) {
        NetworkEntryAdapter adapter = (NetworkEntryAdapter) mFileSystemList.getAdapter();
        List<FileProxy> allFiles = adapter.getFiles();
        List<FileProxy> contents = new ArrayList<FileProxy>();

        for (FileProxy file : allFiles) {
            if (FilenameUtils.wildcardMatch(file.getName(), pattern)) {
                contents.add(file);
            }
        }

        mSelectedFiles.clear();
        if (contents.size() > 0) {
            if (inverseSelection) {
                for (FileProxy file : allFiles) {
                    if (!contents.contains(file)) {
                        mSelectedFiles.add(file);
                    }
                }
            } else {
                for (FileProxy file : contents) {
                    mSelectedFiles.add(file);
                }
            }
        }

        adapter.setSelectedFiles(mSelectedFiles);
        adapter.notifyDataSetChanged();
    }

    private void handleError(NetworkException e) {
        try {
            ErrorDialog.newInstance(e.getLocalizedError()).show(fragmentManager(), "errorDialog");
        } catch (Exception ignore) {}
    }

    private void handleUnlinkedError(NetworkException e) {
        try {
            YesNoDialog.newInstance(e.getLocalizedError(), new YesNoDialog.YesNoDialogListener() {
                @Override
                public void yes() {
                    mDataSource.onUnlinkedAccount();
                }

                @Override
                public void no() {
                }
            }, true).show(fragmentManager(), "errorDialog");
        } catch (Exception ignore) {}
    }

    private void handleErrorAndRetry(NetworkException e, final Runnable command) {
        try {
            YesNoDialog.newInstance(e.getLocalizedError(), new YesNoDialog.YesNoDialogListener() {
                @Override
                public void yes() {
                    command.run();
                }

                @Override
                public void no() {
                    exitFromNetwork();
                }
            }, true).show(fragmentManager(), "errorDialog");
        } catch (Exception ignore) {}
    }

    private class OpenDirectoryTask extends AsyncTask<String, Void, List<FileProxy>> {

        private String mPath;
        private NetworkException mException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setIsLoading(true);
        }

        @Override
        protected List<FileProxy> doInBackground(String ... params) {
            mPath = params[0];
            try {
                List<FileProxy> files = mDataSource.openDirectory(mPath);
                if (mPreSelectedFiles.size() > 0) {
                    ArrayList<String> preSelectedFiles = new ArrayList<String>();
                    for (FileProxy proxy : mPreSelectedFiles) {
                        preSelectedFiles.add(proxy.getFullPath());
                    }
                    mPreSelectedFiles.clear();
                    for (FileProxy fileProxy : files) {
                        if (preSelectedFiles.contains(fileProxy.getFullPath())) {
                            mSelectedFiles.add(fileProxy);
                        }
                    }
                }

                return files;
            } catch (NetworkException e) {
                mException = e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<FileProxy> files) {
            super.onPostExecute(files);
            if (mException != null) {
                switch (mException.getErrorCause()) {
                    case Unlinked_Error:
                        // exit from network, delete account (optional)
                        handleUnlinkedError(mException);
                        exitFromNetwork();
                        break;
                    case FTP_Connection_Closed:
                        // exit from network
                        handleError(mException);
                        exitFromNetwork();
                    case Yandex_Disk_Error:
                        // exit from network
                        handleUnlinkedError(mException);
                        exitFromNetwork();
                        break;
                    case IO_Error: case Common_Error: case Cancel_Error: case Server_error: case Socket_Timeout:
                        // error, propose to retry
                        handleErrorAndRetry(mException, new Runnable() {
                            @Override
                            public void run() {
                                openDirectory(mCurrentPath.getParentPath());
                            }
                        });
                        break;
                }
            } else {
                setIsLoading(false);

                mPath = mDataSource.getPath(mPath);
                setCurrentPath(mPath);

                if (mPath.endsWith("/")) {
                    mPath = mPath.substring(0, mPath.length() - 1);
                }

                String parentPath = mPath.substring(0, mPath.lastIndexOf("/") + 1);
                ListAdapter adapter = mFileSystemList.getAdapter();

                FakeFile upNavigator = new FakeFile("..", mDataSource.getParentPath(parentPath), Extensions.isNullOrEmpty(parentPath));
                if (adapter != null && adapter instanceof NetworkEntryAdapter) {
                    ((NetworkEntryAdapter) adapter).setItems(files, upNavigator);
                    ((NetworkEntryAdapter) adapter).setSelectedFiles(mSelectedFiles);
                } else {
                    mFileSystemList.setAdapter(new NetworkEntryAdapter(files, upNavigator));
                }
                mCurrentPath = new FakeFile(Extensions.isNullOrEmpty(mPath) ? "/" : mPath, mDataSource.getParentPath(parentPath), Extensions.isNullOrEmpty(parentPath));
                mFileSystemList.setSelection(0);
            }
        }
    }

}
