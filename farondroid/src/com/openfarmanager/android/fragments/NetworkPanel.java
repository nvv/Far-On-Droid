package com.openfarmanager.android.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.NetworkEntryAdapter;
import com.openfarmanager.android.core.network.datasource.DataSource;
import com.openfarmanager.android.core.network.datasource.DropboxDataSource;
import com.openfarmanager.android.core.network.datasource.FtpDataSource;
import com.openfarmanager.android.core.network.datasource.GoogleDriveDataSource;
import com.openfarmanager.android.core.network.datasource.IdPathDataSource;
import com.openfarmanager.android.core.network.datasource.MediaFireDataSource;
import com.openfarmanager.android.core.network.datasource.RawPathDataSource;
import com.openfarmanager.android.core.network.datasource.SftpDataSource;
import com.openfarmanager.android.core.network.datasource.SkyDriveDataSource;
import com.openfarmanager.android.core.network.datasource.SmbDataSource;
import com.openfarmanager.android.core.network.datasource.YandexDiskDataSource;
import com.openfarmanager.android.dialogs.CreateBookmarkDialog;
import com.openfarmanager.android.filesystem.FakeFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.model.FileActionEnum;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.SelectParams;
import com.openfarmanager.android.model.exeptions.NetworkException;
import com.openfarmanager.android.model.exeptions.RestoreStoragePathException;
import com.openfarmanager.android.utils.Extensions;
import com.openfarmanager.android.utils.FileUtilsExt;
import com.openfarmanager.android.view.ToastNotification;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.openfarmanager.android.controllers.FileSystemController.*;

/**
 * Panel for 'network filesystem' (like Dropbox, Google Drive etc).
 */
public class NetworkPanel extends MainPanel {

    public static final int MSG_NETWORK_SHOW_PROGRESS = 100000;
    public static final int MSG_NETWORK_HIDE_PROGRESS = 100001;
    public static final int MSG_NETWORK_OPEN = 100002;

    private DataSource mDataSource;
    private FileProxy mCurrentPath;
    private FileProxy mUpNavigator;

    protected FileProxy mLastSelectedFile;
    private NetworkAccount mCurrentNetworkAccount;

    private Dialog mProgressDialog;

    private Subscription mSubscription;
    private OpenDirectoryAction mOpenDirectoryAction = new OpenDirectoryAction();
    private Observable<DirectoryUiInfo> mOpenDirectoryObservable = Observable.create(mOpenDirectoryAction);
    private OpenDirectoryObserver mOpenDirectoryObserver = new OpenDirectoryObserver();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupHandler();
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mFileSystemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final FileProxy file = (FileProxy) adapterView.getItemAtPosition(i);
                if (mIsMultiSelectMode) {
                    updateLongClickSelection(adapterView, file, false);
                    return;
                }
                mSelectedFiles.clear();

                if (i == 0) {
                    if (file.isRoot()) { // exit from network
                        exitFromNetwork();
                    } else {
                        openDirectory(file);
                    }
                } else if (file.isDirectory()) {
                    openDirectory(file);

                    String pathKey = file.getParentPath();
                    if (pathKey.endsWith("/") && !pathKey.equals("/")) {
                        pathKey = pathKey.substring(0, pathKey.length() - 1);
                    }
                    mDirectorySelection.put(pathKey, mFileSystemList.getFirstVisiblePosition() + 1);
                } else {
                    mDataSource.open(file);
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
            return view;
        }

        mCurrentNetworkAccount = App.sInstance.getNetworkApi(getNetworkType()).getCurrentNetworkAccount();

        if (mDataSource.isChangeEncodingSupported()) {
            mCharsetLeft.setVisibility(mPanelLocation == LEFT_PANEL ? View.VISIBLE : View.GONE);
            mCharsetRight.setVisibility(mPanelLocation == RIGHT_PANEL ? View.VISIBLE : View.GONE);

            mCharsetLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gainFocus();
                    mHandler.sendMessage(mHandler.obtainMessage(OPEN_ENCODING_DIALOG, mDataSource.getNetworkTypeEnum()));
                }
            });

            mCharsetRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gainFocus();
                    mHandler.sendMessage(mHandler.obtainMessage(OPEN_ENCODING_DIALOG, mDataSource.getNetworkTypeEnum()));
                }
            });

        }

        mExitLeft.setVisibility(mPanelLocation == LEFT_PANEL ? View.VISIBLE : View.GONE);
        mExitRight.setVisibility(mPanelLocation == RIGHT_PANEL ? View.VISIBLE : View.GONE);

        return view;
    }

    public NetworkAccount getCurrentNetworkAccount() {
        return mCurrentNetworkAccount;
    }

    public void setNetworkType(NetworkEnum networkType) {
        switch (networkType) {
            case Dropbox:
                mDataSource = new DropboxDataSource(mHandler);
                break;
            case SkyDrive:
                mDataSource = new SkyDriveDataSource(mHandler);
                break;
            case FTP:
                mDataSource = new FtpDataSource(mHandler);
                break;
            case SMB:
                mDataSource = new SmbDataSource(mHandler);
                break;
            case YandexDisk:
                mDataSource = new YandexDiskDataSource(mHandler);
                break;
            case GoogleDrive:
                mDataSource = new GoogleDriveDataSource(mHandler);
                break;
            case MediaFire:
                mDataSource = new MediaFireDataSource(mHandler);
                break;
            case SFTP:
                mDataSource = new SftpDataSource(mHandler);
                break;
        }
    }

    public DataSource getDataSource() {
        return mDataSource;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCharsetLeft.setVisibility(View.GONE);
        mCharsetRight.setVisibility(View.GONE);
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    protected boolean onLongClick(AdapterView<?> adapterView, int i) {
        FileProxy file = (FileProxy) adapterView.getItemAtPosition(i);
        if (!file.isUpNavigator() && !file.isVirtualDirectory()) {
            mLastSelectedFile = file;
            updateLongClickSelection(adapterView, file, true);
            if (!file.isVirtualDirectory()) {
                openFileActionMenu();
            }
        }
        return true;
    }

    @Override
    public void createBookmark(final MainPanel inactivePanel) {
        showDialog(new CreateBookmarkDialog(getActivity(), mFileActionHandler, inactivePanel,
                getCurrentPath(), getCurrentPathId(), mCurrentNetworkAccount));
    }

    @Override
    public void selectAll() {
        mSelectedFiles.clear();
        mSelectedFiles.addAll(((NetworkEntryAdapter) mFileSystemList.getAdapter()).getFiles());
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

        setSelectedFilesSizeVisibility();
        calculateSelectedFilesSize();
        showQuickActionPanel();
    }

    protected FileActionEnum[] getAvailableActions() {
        return FileActionEnum.getAvailableActionsForNetwork(mDataSource.getNetworkTypeEnum(), mSelectedFiles);
    }

    public FileProxy getLastSelectedFile() {
        return mLastSelectedFile;
    }

    public void navigateParent() {
        if (mCurrentPath != null && !mCurrentPath.isRoot() && mUpNavigator != null) {
            openDirectory(mUpNavigator);
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
        openDirectory(new FakeFile("/", "/", true));

    }

    public void openDirectoryAndSelect(FileProxy file, List<FileProxy> selectedFiles) {
        mPreSelectedFiles = selectedFiles;
        openDirectory(file);
    }

    public void openBookmark(final String directoryInfo) {
        if (!mIsInitialized) {
            addToPendingList(new Runnable() {
                @Override
                public void run() {
                    openBookmark(directoryInfo);
                }
            });
            return;
        }

        if (directoryInfo == null) {
            openDirectory();
        } else {
            try {
                JSONObject data = new JSONObject(directoryInfo);
                final String id = data.getString(Bookmark.ID);
                final String path = data.getString(Bookmark.PATH);

                if (mDataSource instanceof IdPathDataSource) {
                    setIsLoading(true);
                    Extensions.runAsync(new Runnable() {
                        @Override
                        public void run() {
                            FakeFile proxy = new FakeFile(((IdPathDataSource) mDataSource).requestFileInfo(id));
                            proxy.setFullPath(path);
                            openDirectory(proxy);

                        }
                    });
                } else {
                    String parentPath = FileUtilsExt.getParentPath(path);
                    openDirectory(new FakeFile(id, FileUtilsExt.getFileName(path), parentPath, path,
                            Extensions.isNullOrEmpty(parentPath) || path.equals("/")));
                }
            } catch (Exception e) {
                e.printStackTrace();
                exitFromNetwork();
            }
        }
    }

    public void openDirectory(FileProxy file) {
        openDirectory(file, true);
    }

    public void openDirectory(final FileProxy file, final boolean restorePosition) {
        if (!mIsInitialized) {
            addToPendingList(new Runnable() {
                @Override
                public void run() {
                    openDirectory(file, restorePosition);
                }
            });
            return;
        }

        mOpenDirectoryAction.init(file, restorePosition);
        mOpenDirectoryObserver.setLoading();
        mSubscription = mOpenDirectoryObservable.subscribeOn(Schedulers.computation()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(mOpenDirectoryObserver);
    }

    @Override
    public void invalidate(boolean forceReloadFiles) {
        ListAdapter adapter = mFileSystemList.getAdapter();
        if (forceReloadFiles) {
            openDirectory(mCurrentPath, false);
        } else if (adapter != null && adapter instanceof NetworkEntryAdapter) {
            NetworkEntryAdapter listAdapter = (NetworkEntryAdapter) adapter;
            listAdapter.setSelectedFiles(mSelectedFiles);
            listAdapter.notifyDataSetChanged();
        }
        setSelectedFilesSizeVisibility();
        showQuickActionPanel();
    }

    @Override
    protected void gotoSearchFile(final FileProxy file) {
        final List<FileProxy> selectedFiles = new ArrayList<FileProxy>(1) {{
            add(file);
        }};

        if (mDataSource instanceof RawPathDataSource) {
            openDirectoryAndSelect(mDataSource.createFakeDirectory(file.getParentPath()), selectedFiles);
        } else {
            setIsLoading(true);
            ((IdPathDataSource) mDataSource).createFakeDirectoryAsync(file,
                    new IdPathDataSource.CreateFakeDirectoryCallback() {
                        @Override
                        public void onCreated(final FileProxy file) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    openDirectoryAndSelect(file, selectedFiles);
                                }
                            });
                        }
            });
        }
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

    public String getCurrentPathId() {
        return mCurrentPath.getFullPath();
    }

    protected void onNavigationItemSelected(int pos, List<String> items) {
        try {
            String fullPath = TextUtils.join("/", items.subList(0, pos + 1));
            openDirectory(mDataSource.createFakeDirectory(fullPath.substring(fullPath.length() == 1 ? 0 : 1)));
        } catch (RestoreStoragePathException e) {
            ToastNotification.makeText(App.sInstance.getApplicationContext(),
                    getSafeString(R.string.error_restore_storage_path), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int select(SelectParams selectParams) {

        NetworkEntryAdapter adapter = (NetworkEntryAdapter) mFileSystemList.getAdapter();
        List<FileProxy> allFiles = adapter.getFiles();

        if (selectParams.getType() == SelectParams.SelectionType.NAME) {

            String pattern = selectParams.getSelectionString();

            App.sInstance.getSharedPreferences("action_dialog", 0).edit(). putString("select_pattern", pattern).commit();

            boolean inverseSelection = selectParams.isInverseSelection();
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
        } else {
            if (selectParams.isTodayDate()) {

                Calendar today = Calendar.getInstance();
                Calendar currentDay = Calendar.getInstance();

                for (FileProxy file : allFiles) {
                    currentDay.setTime(new Date(file.lastModifiedDate()));
                    if (isSameDay(today, currentDay)) {
                        mSelectedFiles.add(file);
                    }
                }
            } else {
                long startDate = selectParams.getDateFrom().getTime();
                long endDate = selectParams.getDateTo().getTime();
                for (FileProxy file : allFiles) {
                    if (file.lastModifiedDate() > startDate && file.lastModifiedDate() < endDate) {
                        mSelectedFiles.add(file);
                    }
                }
            }
        }

        adapter.setSelectedFiles(mSelectedFiles);
        adapter.notifyDataSetChanged();

        setSelectedFilesSizeVisibility();
        calculateSelectedFilesSize();
        showQuickActionPanel();

        return mSelectedFiles.size();
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

    private class OpenDirectoryAction implements Observable.OnSubscribe<DirectoryUiInfo> {

        private DirectoryUiInfo directoryInfo = new DirectoryUiInfo();

        public void init(FileProxy directory, boolean restorePosition) {
            directoryInfo.directory = directory;
            directoryInfo.restorePosition = restorePosition;
        }

        @Override
        public void call(Subscriber<? super DirectoryUiInfo> subscriber) {
            try {
                DirectoryScanInfo scanInfo = mDataSource.openDirectory(directoryInfo.directory);

                // due to files object are changed we need to refresh them.
                syncSelectedFiles(mSelectedFiles, scanInfo.files);
                syncSelectedFiles(mPreSelectedFiles, scanInfo.files);

                directoryInfo.files = scanInfo.files;
                directoryInfo.parentPath = scanInfo.parentPath;

                subscriber.onNext(directoryInfo);
            } catch (NetworkException e) {
                subscriber.onError(e);
            }
        }

        private void syncSelectedFiles(List<FileProxy> files, List<FileProxy> allFiles) {
            if (files.size() > 0) {
                ArrayList<String> preSelectedFiles = new ArrayList<>();
                for (FileProxy proxy : files) {
                    preSelectedFiles.add(proxy.getFullPath());
                }
                files.clear();
                for (FileProxy fileProxy : allFiles) {
                    if (preSelectedFiles.contains(fileProxy.getFullPath())) {
                        mSelectedFiles.add(fileProxy);
                    }
                }
            }
        }
    }

    private class OpenDirectoryObserver implements Observer<DirectoryUiInfo> {

        public void setLoading() {
            showQuickActionPanel();
            setSelectedFilesSizeVisibility();
            setIsLoading(true);
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            NetworkException exception = (NetworkException) e;
            switch (exception.getErrorCause()) {
                case Unlinked_Error:
                    // exit from network, delete account (optional)
                    handleUnlinkedError(exception);
                    exitFromNetwork();
                    break;
                case FTP_Connection_Closed:
                    // exit from network
                    handleError(exception);
                    //exitFromNetwork();
                case Yandex_Disk_Error:
                    // exit from network
                    handleUnlinkedError(exception);
                    //exitFromNetwork();
                    break;
                case IO_Error: case Common_Error: case Cancel_Error: case Server_error: case Socket_Timeout: default:
                    // error, propose to retry
                    handleErrorAndRetry(exception, new Runnable() {
                        @Override
                        public void run() {
                            openDirectory(mCurrentPath);
                        }
                    });
                    break;
            }
        }

        @Override
        public void onNext(DirectoryUiInfo directoryInfo) {
            setIsLoading(false);

            FileProxy file = directoryInfo.directory;
            String path = file.getFullPathRaw();

            setCurrentPath(path);
            boolean isRoot = Extensions.isNullOrEmpty(file.getParentPath()) || file.getFullPathRaw().equals("/");
            ListAdapter adapter = mFileSystemList.getAdapter();

            System.out.println("::::::::::::  --->   " + directoryInfo.directory.getFullPathRaw() + "  " + directoryInfo.directory.getParentPath() + "  " + directoryInfo.parentPath);

            mUpNavigator = new FakeFile(file.getParentPath(), "..", directoryInfo.parentPath,
                    FileUtilsExt.getParentPath(file.getFullPathRaw()), isRoot);
            if (adapter != null && adapter instanceof NetworkEntryAdapter) {
                ((NetworkEntryAdapter) adapter).setItems(directoryInfo.files, mUpNavigator);
                ((NetworkEntryAdapter) adapter).setSelectedFiles(mSelectedFiles);
            } else {
                mFileSystemList.setAdapter(new NetworkEntryAdapter(directoryInfo.files, mUpNavigator));
            }
            mCurrentPath = new FakeFile(file.getId(), path, file.getParentPath(), file.getFullPathRaw(), isRoot);

            if (directoryInfo.restorePosition) {
                Integer selection = mDirectorySelection.get(isRoot ? "/" : path);
                mFileSystemList.setSelection(selection != null ? selection : 0);
            }


//            if (mPath == null) {
//                // something very weired
//                exitFromNetwork();
//            }
//
//            mPath = mDataSource.getPath(mPath);
//
//            if (mPath.endsWith("/")) {
//                mPath = mPath.substring(0, mPath.length() - 1);
//            }
//
//            setCurrentPath(Extensions.isNullOrEmpty(mPath) ? "/" : mPath);
//
//            String parentPath = mPath.substring(0, mPath.lastIndexOf("/") + 1);
//            ListAdapter adapter = mFileSystemList.getAdapter();
//
//            FakeFile upNavigator = new FakeFile("..", mDataSource.getParentPath(parentPath), Extensions.isNullOrEmpty(parentPath));
//            if (adapter != null && adapter instanceof NetworkEntryAdapter) {
//                ((NetworkEntryAdapter) adapter).setItems(files, upNavigator);
//                ((NetworkEntryAdapter) adapter).setSelectedFiles(mSelectedFiles);
//            } else {
//                mFileSystemList.setAdapter(new NetworkEntryAdapter(files, upNavigator));
//            }
//            mCurrentPath = new FakeFile(Extensions.isNullOrEmpty(mPath) ? "/" : mPath,
//                    mDataSource.getParentPath(parentPath), Extensions.isNullOrEmpty(parentPath));
//
//            if (mRestorePosition) {
//                Integer selection = mDirectorySelection.get(Extensions.isNullOrEmpty(mPath) ? "/" : mPath);
//                mFileSystemList.setSelection(selection != null ? selection : 0);
//            }

            if (mPreSelectedFiles.size() > 0) {
                calculateSelectedFilesSize();
            }
            showQuickActionPanel();
            setSelectedFilesSizeVisibility();

            if (!mSubscription.isUnsubscribed()) {
                mSubscription.unsubscribe();
            }
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Activity activity = getActivity();

            if (activity == null || activity.isFinishing()) {
                return;
            }

            switch (msg.what) {
                case MSG_NETWORK_SHOW_PROGRESS:
                    showProgressDialog();
                    break;

                case MSG_NETWORK_HIDE_PROGRESS:
                    hideProgressDialog();
                    break;

                case MSG_NETWORK_OPEN:
                    open(msg);
                    break;

                default:
                    NetworkPanel.super.mHandler.sendMessage(Message.obtain(msg));
                    break;
            }
        }
    };

    private void open(Message msg) {
        hideProgressDialog();
        Pair<FileProxy, String> data = (Pair<FileProxy, String>) msg.obj;

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse(data.second), data.first.getMimeType());
        try {
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ActivityNotFoundException) {
                ToastNotification.makeText(App.sInstance.getApplicationContext(),
                        getSafeString(R.string.error_activity_not_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.hide();
            mProgressDialog = null;
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }

        Activity activity = getActivity();

        if (activity == null) {
            return;
        }

        mProgressDialog = new Dialog(activity, android.R.style.Theme_Translucent);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setContentView(R.layout.dialog_progress);

        ((TextView) mProgressDialog.findViewById(R.id.progress_bar_text)).setText(R.string.loading);

        mProgressDialog.show();
    }

    public class DirectoryUiInfo {

        public FileProxy directory;
        public boolean restorePosition;
        public List<FileProxy> files;
        public String parentPath;

    }

    public static class DirectoryScanInfo {
        public List<FileProxy> files;
        public String parentPath;

        public DirectoryScanInfo set(List<FileProxy> files, String parentPath) {
            this.files = files;
            this.parentPath = parentPath;
            return this;
        }
    }

}
