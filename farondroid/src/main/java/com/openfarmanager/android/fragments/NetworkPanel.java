package com.openfarmanager.android.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.FileSystemAdapter;
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
import com.openfarmanager.android.core.network.datasource.WebDavDataSource;
import com.openfarmanager.android.core.network.datasource.YandexDiskDataSource;
import com.openfarmanager.android.dialogs.CreateBookmarkDialog;
import com.openfarmanager.android.filesystem.FakeFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.filter.FileFilter;
import com.openfarmanager.android.filesystem.filter.FileNameFilter;
import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.model.FileActionEnum;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.SelectParams;
import com.openfarmanager.android.model.exeptions.NetworkException;
import com.openfarmanager.android.model.exeptions.RestoreStoragePathException;
import com.openfarmanager.android.utils.Extensions;
import com.openfarmanager.android.utils.FileUtilsExt;
import com.openfarmanager.android.view.ActionBar;
import com.openfarmanager.android.view.NetworkActionBar;
import com.openfarmanager.android.view.ToastNotification;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.openfarmanager.android.controllers.FileSystemController.EXIT_FROM_NETWORK_STORAGE;

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

    protected List<FileProxy> mPreSelectedFiles = new ArrayList<>();

    private CompositeDisposable mSubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupHandler();
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mFileSystemList.setOnItemClickListener(new FileSystemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NetworkEntryAdapter adapter = (NetworkEntryAdapter) mFileSystemList.getAdapter();

                final FileProxy file = (FileProxy) adapter.getItem(position);
                if (mIsMultiSelectMode) {
                    updateLongClickSelection(file, false);
                    return;
                }
                mSelectedFiles.clear();

                if (position == 0) {
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
                    mDirectorySelection.put(pathKey, ((LinearLayoutManager) mFileSystemList.getLayoutManager()).findFirstVisibleItemPosition() + 1);
                } else {
                    mDataSource.open(file);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                onLongClick(position);
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

        mActionBar.updateNavigationItemsVisibility(false, false, isBookmarksSupported());

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
            case WebDav:
                mDataSource = new WebDavDataSource(mHandler);
                break;
        }
    }

    public DataSource getDataSource() {
        return mDataSource;
    }

    @Override
    protected ActionBar createActionBar() {
        return new NetworkActionBar(getContext(), mDataSource.isChangeEncodingSupported(), mDataSource.getNetworkTypeEnum());
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
    public void onDetach() {
        super.onDetach();
        mDirectorySelection.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearSubscription();
    }

    protected void clearSubscription() {
        if (mSubscription != null && !mSubscription.isDisposed()) {
            mSubscription.clear();
        }
    }

    protected boolean onLongClick(int position) {

        FileProxy file = (FileProxy) ((NetworkEntryAdapter) mFileSystemList.getAdapter()).getItem(position);
        if (!file.isUpNavigator() && !file.isVirtualDirectory()) {
            mLastSelectedFile = file;
            updateLongClickSelection(file, true);
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

    protected void updateLongClickSelection(FileProxy file, boolean longClick) {
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

        NetworkEntryAdapter adapter = (NetworkEntryAdapter) mFileSystemList.getAdapter();

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
        clearSubscription();
        mHandler.sendMessage(mHandler.obtainMessage(EXIT_FROM_NETWORK_STORAGE, getPanelLocation()));
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
            addToPendingList(() -> openBookmark(directoryInfo));
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
//                    Extensions.runAsync(new Runnable() {
//                        @Override
//                        public void run() {
//                            FakeFile proxy = new FakeFile(((IdPathDataSource) mDataSource).requestFileInfo(id));
//                            proxy.setFullPath(path);
//                            openDirectory(proxy);
//
//                        }
//                    });

                    Single.create((SingleOnSubscribe<FakeFile>) emitter -> {
                        FakeFile proxy = new FakeFile(((IdPathDataSource) mDataSource).requestFileInfo(id));
                        proxy.setFullPath(path);
                        emitter.onSuccess(proxy);
                    }).subscribeOn(Schedulers.io()).
                            observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<FakeFile>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                            addDisposable(d);
                        }

                        @Override
                        public void onSuccess(FakeFile proxy) {
                            openDirectory(proxy);
                        }

                        @Override
                        public void onError(Throwable e) {
                            handleNetworkException((NetworkException) e);
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
            addToPendingList(() -> openDirectory(file, restorePosition));
            return;
        }

        showQuickActionPanel();
        setSelectedFilesSizeVisibility();
        setIsLoading(true);

        addDisposable(Single.create((SingleOnSubscribe<DirectoryUiInfo>) emitter -> {
            try {
                DirectoryScanInfo scanInfo = mDataSource.openDirectory(file);

                // due to files object are changed we need to refresh them.
                syncSelectedFiles(mSelectedFiles, scanInfo.files);
                syncSelectedFiles(mPreSelectedFiles, scanInfo.files);

                emitter.onSuccess(new DirectoryUiInfo(file, restorePosition, scanInfo.files, scanInfo.parentPath));
            } catch (NetworkException e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(directoryInfo -> {
            setIsLoading(false);

            FileProxy fileProxy = directoryInfo.directory;
            String path = fileProxy.getFullPathRaw();

            setCurrentPath(path);
            boolean isRoot = Extensions.isNullOrEmpty(fileProxy.getParentPath()) || fileProxy.getFullPathRaw().equals("/");
            NetworkEntryAdapter adapter = (NetworkEntryAdapter) mFileSystemList.getAdapter();

            mUpNavigator = new FakeFile(fileProxy.getParentPath(), "..", directoryInfo.parentPath,
                    FileUtilsExt.getParentPath(fileProxy.getFullPathRaw()), isRoot);
            if (adapter != null) {
                adapter.setItems(directoryInfo.files, mUpNavigator);
                adapter.setSelectedFiles(mSelectedFiles);
            } else {
                mFileSystemList.initAdapter(new NetworkEntryAdapter(directoryInfo.files, mUpNavigator));
            }
            mCurrentPath = new FakeFile(fileProxy.getId(), path, fileProxy.getParentPath(), fileProxy.getFullPathRaw(), isRoot);

            if (directoryInfo.restorePosition) {
                Integer selection = mDirectorySelection.get(isRoot ? "/" : path);
                ((LinearLayoutManager) mFileSystemList.getLayoutManager()).scrollToPositionWithOffset(selection != null ? selection : 0, 0);

            }

            if (mPreSelectedFiles.size() > 0) {
                calculateSelectedFilesSize();
            }
            showQuickActionPanel();
            setSelectedFilesSizeVisibility();
        }, throwable -> handleNetworkException((NetworkException) throwable)));

    }

    protected void handleNetworkException(NetworkException e) {
        NetworkException exception = e;
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
            case IO_Error:
            case Common_Error:
            case Cancel_Error:
            case Server_error:
            case Socket_Timeout:
            default:
                // error, propose to retry
                handleErrorAndRetry(exception, () -> openDirectory(mCurrentPath));
                break;
        }
    }

    protected void addDisposable(Disposable d) {
        if (mSubscription == null) {
            mSubscription = new CompositeDisposable();
        }
        mSubscription.add(d);
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

    @Override
    public void invalidate(boolean forceReloadFiles) {
        NetworkEntryAdapter adapter = (NetworkEntryAdapter) mFileSystemList.getAdapter();
        if (forceReloadFiles) {
            openDirectory(mCurrentPath, false);
        } else if (adapter != null) {
            adapter.setSelectedFiles(mSelectedFiles);
            adapter.notifyDataSetChanged();
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
        mActionBar.updateCurrentPath(mDataSource.getNetworkType() + " : " + path);
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

    @Override
    public void openDirectory(String fullPath) {
        try {
            openDirectory(mDataSource.createFakeDirectory(fullPath.substring(fullPath.length() == 1 ? 0 : 1)));
        } catch (RestoreStoragePathException e) {
            ToastNotification.makeText(App.sInstance.getApplicationContext(),
                    getSafeString(R.string.error_restore_storage_path), Toast.LENGTH_SHORT).show();
        }
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

        public DirectoryUiInfo(FileProxy directory, boolean restorePosition, List<FileProxy> files, String parentPath) {
            this.directory = directory;
            this.restorePosition = restorePosition;
            this.files = files;
            this.parentPath = parentPath;
        }

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
