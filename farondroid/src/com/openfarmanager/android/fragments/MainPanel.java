package com.openfarmanager.android.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.*;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.FlatFileSystemAdapter;
import com.openfarmanager.android.controllers.FileSystemController;
import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.core.Settings;
import com.openfarmanager.android.core.archive.ArchiveUtils;
import com.openfarmanager.android.core.archive.MimeTypes;
import com.openfarmanager.android.dialogs.*;
import com.openfarmanager.android.dialogs.CreateArchiveDialog;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemFile;
import com.openfarmanager.android.filesystem.FileSystemScanner;
import com.openfarmanager.android.filesystem.actions.FileActionTask;
import com.openfarmanager.android.filesystem.actions.network.ExportAsTask;
import com.openfarmanager.android.model.FileActionEnum;
import com.openfarmanager.android.model.SelectParams;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.utils.CustomFormatter;
import com.openfarmanager.android.utils.FileUtilsExt;
import com.openfarmanager.android.utils.SystemUtils;
import com.openfarmanager.android.view.ToastNotification;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import static com.openfarmanager.android.controllers.FileSystemController.*;
import static com.openfarmanager.android.model.FileActionEnum.*;

public class MainPanel extends BaseFileSystemPanel {

    public static final int LEFT_PANEL = 0;
    public static final int RIGHT_PANEL = 1;

    public static final int SELECT_ACTION = 100;
    public static final int FILE_CREATE = 1000;
    public static final int FILE_DELETE = 1001;
    public static final int FILE_COPY = 1002;
    public static final int FILE_MOVE = 1003;
    public static final int FILE_CREATE_BOOKMARK = 1004;
    public static final int FILE_EXTRACT_ARCHIVE = 1005;
    public static final int FILE_CREATE_ARCHIVE = 1006;

    private File mBaseDir;
    protected TextView mCurrentPathView;
    protected ListView mFileSystemList;
    protected ProgressBar mProgress;
    protected boolean mIsMultiSelectMode = false;
    protected boolean mIsDataLoading;

    protected List<FileProxy> mSelectedFiles = new ArrayList<FileProxy>();
    protected List<FileProxy> mPreSelectedFiles = new ArrayList<FileProxy>();

    protected View mChangePathToLeft;
    protected View mChangePathToRight;

    protected View mAddToBookmarksLeft;
    protected View mAddToBookmarksRight;

    protected View mNetworkLeft;
    protected View mNetworkRight;

    protected View mHomeLeft;
    protected View mHomeRight;

    protected View mCharsetLeft;
    protected View mCharsetRight;

    protected View mExitLeft;
    protected View mExitRight;

    protected boolean mIsActivePanel;

    private int mLastListPosition;

    protected TextView mSelectedFilesSize;

    protected QuickPopupDialog mQuickActionPopup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupHandler();
        View view = inflater.inflate(R.layout.main_panel, container, false);
        mFileSystemList = (ListView) view.findViewById(android.R.id.list);
        mProgress = (ProgressBar) view.findViewById(R.id.loading);
        mSelectedFilesSize = (TextView) view.findViewById(R.id.selected_files_size);

        mFileSystemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mIsMultiSelectMode) {
                    File file = (File) adapterView.getItemAtPosition(i);
                    updateLongClickSelection(adapterView, file, false);
                    return;
                }
                mSelectedFiles.clear();
                File item = null;
                FileSystemFile file = null;
                Integer previousState = null;
                if (!isRootDirectory()) {
                    if (i == 0) {
                        item = mBaseDir.getParentFile();
                        if (item != null) {
                            previousState = mDirectorySelection.get(item.getAbsolutePath());
                        }
                    }
                }
                if (item == null) {
                    item = (FileSystemFile) adapterView.getItemAtPosition(i);
                }

                if (item instanceof FileSystemFile) {
                    file = (FileSystemFile) item;
                }

                if (file != null && file.isBookmark()) {
                    openDirectory(new File(file.getBookmark().getBookmarkPath()));
                    return;
                }

                if (item.isDirectory()) {
                    openDirectory(item, previousState);
                    if (previousState == null) {
                        mDirectorySelection.put(item.getParent(), mFileSystemList.getFirstVisiblePosition());
                    }
                } else if (item.isFile()) {
                    String mime = ArchiveUtils.getMimeType(item);
                    if (ArchiveUtils.isArchiveSupported(mime)) {
                        mHandler.sendMessage(mHandler.obtainMessage(OPEN_ARCHIVE, item));
                        FlatFileSystemAdapter adapter = (FlatFileSystemAdapter) mFileSystemList.getAdapter();
                        if (adapter != null) {
                            adapter.clearSelectedFiles();
                        }
                    } else if (ArchiveUtils.isCompressionSupported(mime)) {
                        mHandler.sendMessage(mHandler.obtainMessage(OPEN_COMPRESSED_ARCHIVE, item));
                        FlatFileSystemAdapter adapter = (FlatFileSystemAdapter) mFileSystemList.getAdapter();
                        if (adapter != null) {
                            adapter.clearSelectedFiles();
                        }
                    } else {
                        openFile(item);
                    }
                } else if (!item.exists()) {
                    ToastNotification.makeText(App.sInstance.getApplicationContext(),
                            getSafeString(R.string.error_non_existsed_directory), Toast.LENGTH_SHORT).show();
                    openHomeFolder();
                }

            }
        });

        setupGestures(mFileSystemList);

        mFileSystemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                return onLongClick(adapterView, i);
            }
        });

        mQuickActionPopup = new QuickPopupDialog(view, R.layout.quick_action_popup);
        mQuickActionPopup.setPosition((mPanelLocation == LEFT_PANEL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP,
                (int) (50 * getResources().getDisplayMetrics().density));
        View layout = mQuickActionPopup.getContentView();
        layout.findViewById(R.id.quick_action_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gainFocus();
                mHandler.sendMessage(mHandler.obtainMessage(FILE_ACTION, FileActionEnum.COPY));
            }
        });

        layout.findViewById(R.id.quick_action_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gainFocus();
                mHandler.sendMessage(mHandler.obtainMessage(FILE_ACTION, FileActionEnum.DELETE));
            }
        });

        layout.findViewById(R.id.quick_action_deselect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectAll();
                invalidate();
            }
        });

        mFileSystemList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mCurrentPathView = (TextView) view.findViewById(R.id.current_path);
        mCurrentPathView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return openNavigationPathPopup(view);
            }
        });

        mChangePathToLeft = view.findViewById(R.id.change_folder_to_left);
        mChangePathToRight = view.findViewById(R.id.change_folder_to_right);

        mAddToBookmarksLeft = view.findViewById(R.id.add_to_bookmarks_left);
        mAddToBookmarksRight = view.findViewById(R.id.add_to_bookmarks_right);

        mNetworkLeft = view.findViewById(R.id.network_left);
        mNetworkRight = view.findViewById(R.id.network_right);

        mHomeLeft = view.findViewById(R.id.home_left);
        mHomeRight = view.findViewById(R.id.home_right);

        mCharsetLeft = view.findViewById(R.id.charset_left);
        mCharsetRight = view.findViewById(R.id.charset_right);

        mExitLeft = view.findViewById(R.id.exit_left);
        mExitRight = view.findViewById(R.id.exit_right);

        mChangePathToRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.sendMessage(mHandler.obtainMessage(CHANGE_PATH, RIGHT_PANEL));
            }
        });

        mChangePathToLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.sendMessage(mHandler.obtainMessage(CHANGE_PATH, LEFT_PANEL));
            }
        });

        mAddToBookmarksLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gainFocus();
                mHandler.sendMessage(mHandler.obtainMessage(FileSystemController.CREATE_BOOKMARK));
            }
        });

        mAddToBookmarksRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gainFocus();
                mHandler.sendMessage(mHandler.obtainMessage(FileSystemController.CREATE_BOOKMARK));
            }
        });

        mNetworkLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gainFocus();
                mHandler.sendMessage(mHandler.obtainMessage(FileSystemController.OPEN_NETWORK));
            }
        });

        mNetworkRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gainFocus();
                mHandler.sendMessage(mHandler.obtainMessage(FileSystemController.OPEN_NETWORK));
            }
        });

        mCharsetLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gainFocus();
                mHandler.sendMessage(mHandler.obtainMessage(FileSystemController.OPEN_ENCODING_DIALOG));
            }
        });

        mCharsetRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gainFocus();
                mHandler.sendMessage(mHandler.obtainMessage(FileSystemController.OPEN_ENCODING_DIALOG));
            }
        });

        mExitLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gainFocus();
                mHandler.sendMessage(mHandler.obtainMessage(EXIT_FROM_NETWORK_STORAGE, mPanelLocation));
            }
        });

        mExitRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gainFocus();
                mHandler.sendMessage(mHandler.obtainMessage(EXIT_FROM_NETWORK_STORAGE, mPanelLocation));
            }
        });

        mHomeLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeFolder();
            }
        });

        mHomeRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeFolder();
            }
        });

        postInitialization();
        setNavigationButtonsVisibility();

        return view;
    }

    private void openHomeFolder() {
        gainFocus();
        try {
            openDirectory(new File(App.sInstance.getSettings().getHomeFolder()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unselectAll() {
        mSelectedFiles.clear();
    }

    protected void onNavigationItemSelected(int pos, List<String> items) {
        File f = new File(TextUtils.join("/", items.subList(0, pos + 1)));
        if (f.exists() && f.canRead()) {
            openDirectory(f);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    protected boolean onLongClick(AdapterView<?> adapterView, int i) {
        FileSystemFile file = (FileSystemFile) adapterView.getItemAtPosition(i);
        if (!file.isUpNavigator()) {
            mLastSelectedFile = file;
            updateLongClickSelection(adapterView, file, true);
            if (!file.isVirtualDirectory()) {
                openFileActionMenu();
            }
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isFileSystemPanel()) {
            openDirectory(mBaseDir != null ? mBaseDir : FileSystemScanner.sInstance.getRoot());
        }
        mFileSystemList.setSelection(mLastListPosition);
    }

    public void onResume() {
        super.onResume();
        // selected files need to be updated after application resumes
        FlatFileSystemAdapter adapter = (FlatFileSystemAdapter) mFileSystemList.getAdapter();
        if (adapter != null && mSelectedFiles.size() > 0) {
            adapter.setSelectedFiles(mSelectedFiles);
            adapter.notifyDataSetChanged();
        }

        setIsActivePanel(mIsActivePanel);
        setNavigationButtonsVisibility();

        int color = App.sInstance.getSettings().getMainPanelColor();
        mChangePathToLeft.setBackgroundColor(color);
        mChangePathToRight.setBackgroundColor(color);
        mAddToBookmarksLeft.setBackgroundColor(color);
        mAddToBookmarksRight.setBackgroundColor(color);
        mNetworkLeft.setBackgroundColor(color);
        mNetworkRight.setBackgroundColor(color);
        mHomeLeft.setBackgroundColor(color);
        mHomeRight.setBackgroundColor(color);
        mExitLeft.setBackgroundColor(color);
        mExitRight.setBackgroundColor(color);

        mSelectedFilesSize.setBackgroundColor(App.sInstance.getSettings().getSecondaryColor());
        mSelectedFilesSize.setTextColor(App.sInstance.getSettings().getSelectedColor());
    }

    @Override
    public void onStop() {
        super.onStop();
        // Save ListView state
        mLastListPosition = mFileSystemList.getFirstVisiblePosition();
    }

    @Override
    public void onDetach () {
        super.onDetach();
        if (mQuickActionPopup != null) {
            getSelectedFiles().clear();
            mQuickActionPopup.dismiss();
        }
    }

    protected void updateLongClickSelection(AdapterView<?> adapterView, File file, boolean longClick) {
        FileSystemFile systemFile = (FileSystemFile) file;

        if (systemFile.isVirtualDirectory()) {
            ToastNotification.makeText(App.sInstance.getApplicationContext(),
                    getSafeString(R.string.error_virtual_directories_not_permitted), Toast.LENGTH_SHORT).show();
            return;
        }

        if(systemFile.isUpNavigator()) {
            return;
        }
        if (mSelectedFiles.contains(file) && !longClick) {
            mSelectedFiles.remove(file);
        } else {
            if (mSelectedFiles.contains(file)) {
                return;
            }
            mSelectedFiles.add(0, systemFile);
        }
        ((FlatFileSystemAdapter) adapterView.getAdapter()).setSelectedFiles(mSelectedFiles);
        ((BaseAdapter) adapterView.getAdapter()).notifyDataSetChanged();

        setSelectedFilesSizeVisibility();
        calculateSelectedFilesSize();
        showQuickActionPanel();
    }

    protected void calculateSelectedFilesSize() {
        long size = 0;
        for (FileProxy f : mSelectedFiles) {
            size += f.isDirectory() ? 0 : f.getSize();
        }

        mSelectedFilesSize.setText(getString(R.string.selected_files, CustomFormatter.formatBytes(size), mSelectedFiles.size()));
    }

    protected void showQuickActionPanel() {

        if (mQuickActionPopup == null) {
            return;
        }

        boolean showPanel = (isFileSystemPanel() || this instanceof NetworkPanel) &&
                App.sInstance.getSettings().isShowQuickActionPanel() && getSelectedFilesCount() > 0;

        try {

            if (showPanel) {
                mQuickActionPopup.show();
            } else {
                mQuickActionPopup.dismiss();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openFileActionMenu() {
        showDialog(new com.openfarmanager.android.dialogs.FileActionDialog(getActivity(),
                getAvailableActions(), mFileActionHandler));
    }

    protected FileActionEnum[] getAvailableActions() {
        return FileActionEnum.getAvailableActions(getSelectedFiles(), mLastSelectedFile);
    }

    public void executeAction(FileActionEnum action, final MainPanel inactivePanel) {
        boolean twoPanelAction = action == MOVE || action == COPY || action == ARCHIVE_EXTRACT;
        if (twoPanelAction) {
            if (inactivePanel == null) {
                ToastNotification.makeText(App.sInstance.getApplicationContext(),
                        getSafeString(R.string.error_quick_view_operation_not_supported), Toast.LENGTH_SHORT).show();
                return;
            } else if (inactivePanel instanceof ArchivePanel) {
                ToastNotification.makeText(App.sInstance.getApplicationContext(),
                        getSafeString(R.string.error_archive_operation_not_supported), Toast.LENGTH_SHORT).show();
                return;
            } else if (!(inactivePanel instanceof NetworkPanel) && !inactivePanel.getCurrentDir().exists()) {
                ToastNotification.makeText(App.sInstance.getApplicationContext(),
                        getSafeString(R.string.error_virtual_directories_not_permitted), Toast.LENGTH_SHORT).show();
                return;
            } else if (this instanceof NetworkPanel && inactivePanel instanceof NetworkPanel) {
                ToastNotification.makeText(App.sInstance.getApplicationContext(),
                        getSafeString(R.string.error_network_operation_not_supported), Toast.LENGTH_SHORT).show();
                return;
            } else if (inactivePanel.isDataLoading()) {
                ToastNotification.makeText(App.sInstance.getApplicationContext(),
                        getSafeString(R.string.error_inactive_panel_is_busy), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        int count = getSelectedFilesCount();
        if (count == 0 && action != SET_AS_HOME) { // no operations can be performed
            return;
        }

        switch (action) {
            case INFO:
                info();
                break;
            case SET_AS_HOME:
                App.sInstance.getSettings().setHomeFolder(mBaseDir.getAbsolutePath());
                ToastNotification.makeText(App.sInstance.getApplicationContext(), getString(R.string.home_folder_updated), Toast.LENGTH_LONG).show();
                break;
            case SEND:
                send();
                break;
            case DELETE:
                delete(inactivePanel);
                break;
            case MOVE:
                move(inactivePanel, false);
                break;
            case COPY:
                copy(inactivePanel);
                break;
            case EDIT:
                Message message = mHandler.obtainMessage(FILE_OPEN, mLastSelectedFile.getAbsoluteFile());
                message.arg1 = ARG_FORCE_OPEN_FILE_IN_EDITOR;
                mHandler.sendMessage(message);
                break;
            case RENAME:
                move(inactivePanel, true);
                break;
            case FILE_OPEN_WITH:
                openAs(mLastSelectedFile);
                break;
            case ARCHIVE_EXTRACT:
                extractArchive(inactivePanel);
                break;
            case CREATE_ARCHIVE:
                createArchive(inactivePanel);
                break;
            case EXPORT_AS:
                mHandler.sendMessage(mHandler.obtainMessage(FileSystemController.EXPORT_AS, mSelectedFiles.size() == 1 ? mSelectedFiles.get(0) : null));
                break;
            case OPEN_WEB:
                mHandler.sendMessage(mHandler.obtainMessage(FileSystemController.OPEN_WEB, mSelectedFiles.size() == 1 ? mSelectedFiles.get(0) : null));
                break;
            case COPY_PATH:
                if (mSelectedFiles.size() == 1) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) App.sInstance.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(mSelectedFiles.get(0).getFullPathRaw());
                    ToastNotification.makeText(App.sInstance.getApplicationContext(), getString(R.string.path_copied), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void info() {
        try {
            InfoDialog.newInstance(mLastSelectedFile.getAbsolutePath()).show(fragmentManager(), "confirmDialog");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send() {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mLastSelectedFile));
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(mLastSelectedFile).toString());
        shareIntent.setType(MimeTypes.lookupMimeType(extension));

        startActivity(Intent.createChooser(shareIntent, getSafeString(R.string.action_share_to)));
    }

    public void move(final MainPanel inactivePanel, boolean forceRename) {
        boolean rename = forceRename;

        if (inactivePanel.isFileSystemPanel() && isFileSystemPanel()) {
            boolean isTheSameFolders = FileUtilsExt.isTheSameFolders(getSelectedFiles(), inactivePanel.getCurrentDir());
            rename = forceRename || isTheSameFolders && getSelectedFiles().size() == 1;
        }

        showDialog(new CopyMoveFileDialog(getActivity(), mFileActionHandler, inactivePanel, rename,
                rename ? getSelectedFileProxies().get(0).getName() : inactivePanel.getCurrentPath()));
    }

    public void createBookmark(final MainPanel inactivePanel) {
        showDialog(new CreateBookmarkDialog(getActivity(), mFileActionHandler, inactivePanel, mBaseDir.getAbsolutePath()));
    }

    public void delete(final MainPanel inactivePanel) {
        showDialog(new DeleteFileDialog(getActivity(), mFileActionHandler, inactivePanel));
    }

    public void createFile(final MainPanel inactivePanel) {
        showDialog(new CreateFileDialog(getActivity(), mFileActionHandler, inactivePanel, this instanceof NetworkPanel));
    }

    public void showDialog(Dialog dialog) {
        dialog.show();
        adjustDialogSize(dialog);
    }

    public void showSelectDialog() {
        showDialog(new SelectDialog(getActivity(), mSelectFilesCommand));
    }

    public void showSearchDialog() {
        try {
            if (!isSearchSupported()) {
                ToastNotification.makeText(App.sInstance.getApplicationContext(),
                        getSafeString(R.string.error_search_not_supported, getPanelType()), Toast.LENGTH_SHORT).show();
                return;
            }

            final boolean isNetworkPanel = this instanceof NetworkPanel;
            SearchActionDialog.newInstance(isNetworkPanel, new SearchActionDialog.OnSearchConfirmedListener() {
                @Override
                public void onSearchConfirmed(String fileMask, String keyword, boolean isCaseSensitive, boolean isWholeWords) {
                    try {
                        SearchResult.newInstance(isNetworkPanel, isNetworkPanel ? ((NetworkPanel) MainPanel.this).getNetworkType() : null,
                                getCurrentPath(), fileMask, keyword, isCaseSensitive, isWholeWords, new SearchResult.SearchResultListener() {
                            @Override
                            public void onGotoFile(final FileProxy f) {
                                if (MainPanel.this instanceof NetworkPanel) {
                                    ((NetworkPanel) MainPanel.this).openDirectoryAndSelect(f.getParentPath(),
                                            new ArrayList<FileProxy>() {{ add(f); }});

                                } else {
                                    File file = (FileSystemFile) f;
                                    setCurrentDir(file.isDirectory() ? file : file.getParentFile());
                                    invalidate();
                                }
                            }

                            @Override
                            public void onViewFile(FileProxy f) {
                                File file = (FileSystemFile) f;
                                if (file.isDirectory()) {
                                    setCurrentDir(file);
                                    invalidate();
                                } else {
                                    openFile(file);
                                }
                            }

                            @Override
                            public void onResetSearch() {
                                showSearchDialog();
                            }
                        }).show(fragmentManager(), "searchResult");
                    } catch (Exception e) {
                    }
                }
            }).show(fragmentManager(), "confirmDialog");
        } catch (Exception e) {
        }
    }

    public void copy(final MainPanel inactivePanel) {
        showDialog(new CopyMoveFileDialog(getActivity(), mFileActionHandler, inactivePanel));
    }

    public void export(final MainPanel inactivePanel, String downloadLink, String destination) {
        FileActionTask task = null;
        try {
            task = new ExportAsTask(fragmentManager(),
                    new FileActionTask.OnActionListener() {
                        @Override
                        public void onActionFinish(TaskStatusEnum status) {
                            try {
                                if (status != TaskStatusEnum.OK) {
                                    try {
                                        String error = status.getNetworkErrorException().getLocalizedError();
                                        ErrorDialog.newInstance(error).show(fragmentManager(), "errorDialog");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            invalidatePanels(inactivePanel);
                        }
                    }, downloadLink, destination);
        } catch (Exception e) {
            e.printStackTrace();
        }
        task.execute();
    }

    public void extractArchive(final MainPanel inactivePanel) {
        String extractFileName = mLastSelectedFile.getName();
        try {
            extractFileName = mLastSelectedFile.getName().split("\\.")[0];
        } catch (Exception ignore) { }
        String defaultPath = inactivePanel.getCurrentDir().getAbsolutePath();
        if (!defaultPath.endsWith(File.separator)) {
            defaultPath += File.separator;
        }
        defaultPath += extractFileName;

        final boolean isCompressed = ArchiveUtils.isCompressionSupported(mLastSelectedFile);

        showDialog(new ExtractArchiveDialog(getActivity(), mFileActionHandler, inactivePanel, isCompressed, defaultPath));
    }

    public void createArchive(final MainPanel inactivePanel) {
        showDialog(new CreateArchiveDialog(getActivity(), mFileActionHandler, inactivePanel,
                mSelectedFiles.size() > 0 ? mSelectedFiles.get(0).getName() : ""));
    }

    public void invalidatePanels(MainPanel inactivePanel) {
        getSelectedFiles().clear();
        invalidate();
        // inactivePanel may be null when 'quick view' is opened.
        if (inactivePanel != null) {
            try {
                inactivePanel.invalidate();
            } catch (Exception ignore) {

            }
        }
    }

    public void restoreState(String currentPath, boolean isActive) {
        File currentFile = new File(currentPath);
        setCurrentDir(currentFile);
        openDirectory(currentFile);
        setIsActivePanel(isActive);
    }

    public void invalidate() {
        FlatFileSystemAdapter adapter = (FlatFileSystemAdapter) mFileSystemList.getAdapter();
        adapter.setBaseDir(mBaseDir);
        adapter.setSelectedFiles(mSelectedFiles);
        adapter.notifyDataSetChanged();
        setSelectedFilesSizeVisibility();
        showQuickActionPanel();
    }

    private void openFile(File item) {
        mHandler.sendMessage(mHandler.obtainMessage(FILE_OPEN, item.getAbsoluteFile()));
    }

    private void openAs(File item) {
        mHandler.sendMessage(mHandler.obtainMessage(OPEN_WITH, item.getAbsoluteFile()));
    }

    public void setIsMultiSelectMode(boolean value) {
        mIsMultiSelectMode = value;
    }

    public boolean switchMultiSelectMode() {
        return (mIsMultiSelectMode = !mIsMultiSelectMode);
    }

    public void setupHandler() {
        if (App.sInstance.getFileSystemController() != null) {
            mHandler = App.sInstance.getFileSystemController().getPanelHandler();
        }
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public FileProxy getLastSelectedFile() {
        return mLastSelectedFile instanceof FileProxy ? (FileProxy) mLastSelectedFile : null;
    }

    public List<FileProxy> getSelectedFileProxies() {
        return mSelectedFiles;
    }

    public int getSelectedFilesCount() {
        return mSelectedFiles.size();
    }

    public List<File> getSelectedFiles() {
        try {
            if (mSelectedFiles.size() == 0 && mFileSystemList != null && mFileSystemList.getSelectedItem() instanceof FileSystemFile) {
                mSelectedFiles.add((FileSystemFile) mFileSystemList.getSelectedItem());
            }
        } catch (Exception ignore) {}

        //noinspection unchecked
        return (List) mSelectedFiles;
    }

    // TODO: for list of files use always Set instead of List (to avoid duplicated files).
    public Set<File> getAllFiles() {
        LinkedHashSet<File> allFiles = new LinkedHashSet<File>();
        try {
            Collections.addAll(allFiles, mBaseDir.listFiles());
        } catch (Exception ignore) {}
        return allFiles;
    }

    public String getCurrentPath() {
        return mBaseDir.getAbsolutePath();
    }

    public File getCurrentDir() {
        return mBaseDir;
    }

    public void setCurrentDir(File dir) {
        mBaseDir = dir;
    }

    public boolean isActive() {
        return mIsActivePanel;
    }

    public void setIsActivePanel(final boolean active) {
        if (!mIsInitialized) {
            addToPendingList(new Runnable() {
                @Override
                public void run() {
                    setIsActivePanel(active);
                }
            });
            return;
        }

        mIsActivePanel = active;
        if (mCurrentPathView != null) {
            mCurrentPathView.setSelected(mIsActivePanel);

            mCurrentPathView.setBackgroundColor(mIsActivePanel ?
                    App.sInstance.getSettings().getSecondaryColor() : App.sInstance.getSettings().getMainPanelColor());

        }
        if (active && mFileSystemList != null) {
            mFileSystemList.requestFocus();
        }
    }

    public int getPanelLocation() {
        return mPanelLocation;
    }

    public void setPanelLocation(int location) {
        mPanelLocation = location;
        setNavigationButtonsVisibility();
    }

    public void setNavigationButtonsVisibility() {
        setNavigationButtonsVisibility(false);
    }

    public void forceHideNavigationButtons() {
        setNavigationButtonsVisibility(true);
    }

    private void setNavigationButtonsVisibility(final boolean forceHide) {
        if (!mIsInitialized) {
            addToPendingList(new Runnable() {
                @Override
                public void run() {
                    setNavigationButtonsVisibility(forceHide);
                }
            });
            return;
        }

        boolean isCopyFolderSupported = isCopyFolderSupported();
        mChangePathToLeft.setVisibility(!forceHide && isCopyFolderSupported && mPanelLocation == LEFT_PANEL ? View.VISIBLE : View.GONE);
        mChangePathToRight.setVisibility(!forceHide && isCopyFolderSupported && mPanelLocation == RIGHT_PANEL ? View.VISIBLE : View.GONE);

        mAddToBookmarksLeft.setVisibility(!forceHide && isCopyFolderSupported && mPanelLocation == LEFT_PANEL ? View.VISIBLE : View.GONE);
        mAddToBookmarksRight.setVisibility(!forceHide && isCopyFolderSupported && mPanelLocation == RIGHT_PANEL ? View.VISIBLE : View.GONE);

        mNetworkLeft.setVisibility(!forceHide && isCopyFolderSupported && mPanelLocation == LEFT_PANEL ? View.VISIBLE : View.GONE);
        mNetworkRight.setVisibility(!forceHide && isCopyFolderSupported && mPanelLocation == RIGHT_PANEL ? View.VISIBLE : View.GONE);


        boolean isHomeFolderEnabled = App.sInstance.getSettings().isEnableHomeFolder();
        mHomeLeft.setVisibility(!forceHide && isCopyFolderSupported && isHomeFolderEnabled && mPanelLocation == LEFT_PANEL ?
                View.VISIBLE : View.GONE);
        mHomeRight.setVisibility(!forceHide && isCopyFolderSupported && isHomeFolderEnabled && mPanelLocation == RIGHT_PANEL ?
                View.VISIBLE : View.GONE);
    }

    public boolean isRootDirectory() {
        if (App.sInstance.getSettings().isSDCardRoot()) {
            return mBaseDir.getAbsolutePath().equals(Settings.sSdPath);
        }
        return mBaseDir.getAbsolutePath().equals("/");
    }

    /**
     * Is 'move to folder' supported - buttons near the path to change path of inactive panel.
     * Must be ovveriden for subclasses.
     *
     * @return <code>true</code>
     */
    protected boolean isCopyFolderSupported() {
        return true;
    }

    public void openDirectory(final File directory) {
        openDirectory(directory, -1);
    }

    /**
     * Open directory with "selection", i.e. scrolling file system list to certain position.
     *
     * @param directory folder to be opened.
     * @param selection position in list to be selected (scrolled).
     */
    private void openDirectory(final File directory, final Integer selection) {

        if (!mIsInitialized) {
            addToPendingList(new Runnable() {
                @Override
                public void run() {
                    openDirectory(directory, selection);
                }
            });
            return;
        }

        setSelectedFilesSizeVisibility();
        showQuickActionPanel();

        File oldDir = mBaseDir;
        mBaseDir = directory.getAbsoluteFile();
        mCurrentPathView.setText(mBaseDir.getAbsolutePath());
        FlatFileSystemAdapter adapter = (FlatFileSystemAdapter) mFileSystemList.getAdapter();
        if (adapter == null) {
            mFileSystemList.setAdapter(new FlatFileSystemAdapter(mBaseDir, mOnFolderScannedListener));
        } else {
            adapter.resetFilter();
            adapter.setBaseDir(mBaseDir, selection);
            sendEmptyMessage(DIRECTORY_CHANGED);
        }
        if(adapter != null && oldDir != null){
            mFileSystemList.setSelection(adapter.getItemPosition(oldDir));
        }
    }

    protected void setSelectedFilesSizeVisibility() {
        mSelectedFilesSize.setVisibility((!App.sInstance.getSettings().isShowSelectedFilesSize() || mSelectedFiles.size() == 0) ?
                View.GONE : View.VISIBLE);
    }

    FlatFileSystemAdapter.OnFolderScannedListener mOnFolderScannedListener = new FlatFileSystemAdapter.OnFolderScannedListener() {
        @Override
        public void onScanFinished(Integer selection) {
            if (selection != null) {
                mFileSystemList.setSelection(selection);
            }
        }
    };

    private void sendEmptyMessage(int message) {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(message);
        }
    }

    @Override
    public int select(SelectParams selectParams) {

        if (mBaseDir == null) {
            // handle unexpected situation.
            return 0;
        }

        mSelectedFiles.clear();
        if (selectParams.getType() == SelectParams.SelectionType.NAME) {

            String pattern = selectParams.getSelectionString();
            boolean inverseSelection = selectParams.isInverseSelection();

            App.sInstance.getSharedPreferences("action_dialog", 0).edit(). putString("select_pattern", pattern).commit();

            FileFilter select = new WildcardFileFilter(pattern);
            File[] contents = mBaseDir.listFiles(select);

            if (contents != null) {
                if (inverseSelection) {
                    File[] allFiles = mBaseDir.listFiles();
                    List selection = Arrays.asList(contents);
                    for (File file : allFiles) {
                        if (!selection.contains(file)) {
                            mSelectedFiles.add(new FileSystemFile(file.getAbsolutePath()));
                        }
                    }
                } else {
                    for (File file : contents) {
                        mSelectedFiles.add(new FileSystemFile(file.getAbsolutePath()));
                    }
                }
            }
        } else {
            File[] allFiles = mBaseDir.listFiles();
            if (selectParams.isTodayDate()) {

                Calendar today = Calendar.getInstance();
                Calendar currentDay = Calendar.getInstance();

                for (File file : allFiles) {
                    currentDay.setTime(new Date(file.lastModified()));
                    if (isSameDay(today, currentDay)) {
                        mSelectedFiles.add(new FileSystemFile(file.getAbsolutePath()));
                    }
                }
            } else {
                long startDate = selectParams.getDateFrom().getTime();
                long endDate = selectParams.getDateTo().getTime();
                for (File file : allFiles) {
                    if (file.lastModified() > startDate && file.lastModified() < endDate) {
                        mSelectedFiles.add(new FileSystemFile(file.getAbsolutePath()));
                    }
                }
            }

        }

        FlatFileSystemAdapter adapter = (FlatFileSystemAdapter) mFileSystemList.getAdapter();
        adapter.setSelectedFiles(mSelectedFiles);
        adapter.notifyDataSetChanged();
        setSelectedFilesSizeVisibility();
        calculateSelectedFilesSize();
        showQuickActionPanel();

        return mSelectedFiles.size();
    }

    /**
     * <p>Checks if two calendars represent the same day ignoring time.</p>
     * @param cal1  the first calendar, not altered, not null
     * @param cal2  the second calendar, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either calendar is <code>null</code>
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public void addSelectedFiles(LinkedHashSet<File> selectedFiles) {
        for (File file : selectedFiles) {
            FileSystemFile systemFile = new FileSystemFile(file.getAbsolutePath());
            if (!mSelectedFiles.contains(systemFile)) {
                mSelectedFiles.add(systemFile);
            }
        }

        final FlatFileSystemAdapter adapter = (FlatFileSystemAdapter) mFileSystemList.getAdapter();
        adapter.setSelectedFiles(mSelectedFiles);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void navigateParent() {
        if (!isRootDirectory()) {
            // get parent file
            File parentFile = mBaseDir.getParentFile();
            // in case of parent file is null (for example, mBaseDir is already root. this shouldn't be happened)
            // use current dir.
            openDirectory(parentFile == null ? mBaseDir : parentFile);
        }
    }

    /**
     * Post runnable command with handle if fragment is attached to activity.
     *
     * @param runnable task to pe executed.
     */
    protected void postIfAttached(Runnable runnable) {
        //if (isAdded()) {
            mHandler.post(runnable);
        //}
    }

    public void navigateRoot() {
        if (!isRootDirectory()) {
            openDirectory(new File(FileSystemScanner.ROOT));
        }
    }

    public void filter(String obj) {
        ((FlatFileSystemAdapter) mFileSystemList.getAdapter()).filter(obj);
    }

    public void selectCurrentFile(int direction) {
        int position = mFileSystemList.getSelectedItemPosition();
        FlatFileSystemAdapter adapter = (FlatFileSystemAdapter) mFileSystemList.getAdapter();
        FileProxy currentFile;

        try {
            currentFile = (FileProxy) adapter.getItem(position);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (position > 0) {
            if (mSelectedFiles.contains(currentFile)) {
                mSelectedFiles.remove(currentFile);
            } else {
                mSelectedFiles.add(currentFile);
            }

            adapter.setSelectedFiles(mSelectedFiles);
            adapter.notifyDataSetChanged();
        }

        mFileSystemList.setSelectionFromTop(position + direction, mFileSystemList.getSelectedView() != null ?
                (int) mFileSystemList.getSelectedView().getY() : 0);
    }

    protected void setIsLoading(boolean isLoading) {
        mProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mFileSystemList.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        mCurrentPathView.setVisibility(isLoading ? View.GONE : View.VISIBLE);

        if (isActive()) {
            mFileSystemList.requestFocus();
        }

        mIsDataLoading = isLoading;
    }

    private void adjustDialogSize(Dialog dialog) {
        adjustDialogSize(dialog, 0.8f);
    }

    /**
     * Adjust dialog size. Actuall for old android version only (due to absence of Holo themes).
     *
     * @param dialog dialog whose size should be adjusted.
     */
    private void adjustDialogSize(Dialog dialog, float scaleFactor) {
        if (!SystemUtils.isHoneycombOrNever()) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(dialog.getWindow().getAttributes());
            params.width = (int) (metrics.widthPixels * scaleFactor);
            params.height = (int) (metrics.heightPixels * scaleFactor);

            dialog.getWindow().setAttributes(params);
        }
    }

    protected Handler mFileActionHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FILE_CREATE:
                    CreateFileDialog.NewFileResult newFileResult = (CreateFileDialog.NewFileResult) msg.obj;
                    getCreateNewCommand().execute(newFileResult.inactivePanel,
                            newFileResult.destination, newFileResult.isFolder);
                    break;
                case FILE_DELETE:
                    DeleteFileDialog.DeleteFileResult deleteFileResult = (DeleteFileDialog.DeleteFileResult) msg.obj;
                    getDeleteCommand(deleteFileResult.inactivePanel, getLastSelectedFile()).execute(
                            deleteFileResult.inactivePanel, deleteFileResult.destination);
                    break;
                case FILE_COPY:
                    CopyMoveFileDialog.CopyMoveFileResult copyMoveFileResult = (CopyMoveFileDialog.CopyMoveFileResult) msg.obj;
                    getCopyToCommand(copyMoveFileResult.inactivePanel).execute(
                            copyMoveFileResult.inactivePanel, copyMoveFileResult.destination);
                    break;
                case FILE_MOVE:
                    copyMoveFileResult = (CopyMoveFileDialog.CopyMoveFileResult) msg.obj;
                    getMoveCommand(copyMoveFileResult.inactivePanel).execute(copyMoveFileResult.inactivePanel,
                            copyMoveFileResult.destination, null, copyMoveFileResult.isRename);
                    break;
                case FILE_CREATE_BOOKMARK:
                    CreateBookmarkDialog.CreateBookmarkResult createBookmarkResult = (CreateBookmarkDialog.CreateBookmarkResult) msg.obj;
                    mCreateBookmarkCommand.execute(createBookmarkResult.inactivePanel, createBookmarkResult.link,
                            null, createBookmarkResult.label);
                    break;
                case FILE_EXTRACT_ARCHIVE:
                    ExtractArchiveDialog.ExtractArchiveResult extractArchiveResult = (ExtractArchiveDialog.ExtractArchiveResult) msg.obj;
                    mExtractArchiveCommand.execute(extractArchiveResult.inactivePanel, extractArchiveResult.destination,
                            null, extractArchiveResult.isCompressed);
                    break;
                case FILE_CREATE_ARCHIVE:
                    CreateArchiveDialog.CreateArchiveResult createArchiveResult = (CreateArchiveDialog.CreateArchiveResult) msg.obj;
                    mCreateArchiveCommand.execute(createArchiveResult.inactivePanel, createArchiveResult.archiveName,
                            createArchiveResult.archiveType, createArchiveResult.isCompressionEnabled, createArchiveResult.compression);
                    break;
                case SELECT_ACTION:
                    mHandler.sendMessage(Message.obtain(mHandler, FILE_ACTION, msg.obj));
                    break;
            }
        }
    };

    protected boolean isDataLoading() {
        return mIsDataLoading;
    }

    public boolean isFileSystemPanel() {
        return true;
    }

    public boolean isSearchSupported() {
        return true;
    }

    public String getPanelType() {
        return "File System";
    }
}