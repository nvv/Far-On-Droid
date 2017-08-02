package com.openfarmanager.android.controllers;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.openfarmanager.android.App;
import com.openfarmanager.android.FileView;
import com.openfarmanager.android.Help;
import com.openfarmanager.android.R;
import com.openfarmanager.android.SettingsActivity;
import com.openfarmanager.android.adapters.ApplicationChooserAdapter;
import com.openfarmanager.android.adapters.ExportAsAdapter;
import com.openfarmanager.android.adapters.MimeTypeChooserAdapter;
import com.openfarmanager.android.core.Settings;
import com.openfarmanager.android.core.network.NetworkConnectionManager;
import com.openfarmanager.android.dialogs.BookmarksListDialog;
import com.openfarmanager.android.dialogs.SelectEncodingDialog;
import com.openfarmanager.android.filesystem.DropboxFile;
import com.openfarmanager.android.filesystem.GoogleDriveFile;
import com.openfarmanager.android.filesystem.actions.DiffDirectoriesTask;
import com.openfarmanager.android.filesystem.actions.RootTask;
import com.openfarmanager.android.filesystem.actions.network.DropboxTask;
import com.openfarmanager.android.fragments.ArchivePanel;
import com.openfarmanager.android.fragments.BasePanel;
import com.openfarmanager.android.fragments.DirectoryDetailsView;
import com.openfarmanager.android.fragments.GenericPanel;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.model.FileActionEnum;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.utils.SystemUtils;
import com.openfarmanager.android.view.ExpandPanelAnimation;
import com.openfarmanager.android.view.ToastNotification;
import com.openfarmanager.android.view.panels.MainToolbar;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashSet;
import java.util.List;

import static com.openfarmanager.android.fragments.MainPanel.ARG_PANEL_LOCATION;
import static com.openfarmanager.android.fragments.MainPanel.LEFT_PANEL;
import static com.openfarmanager.android.fragments.MainPanel.RIGHT_PANEL;

public class FileSystemController {

    private static enum PanelsState {
        LEFT_EXPANDED, EQUALS, RIGHT_EXPANDED
    }

    public static final int ALT_DOWN = 0;
    public static final int ALT_UP = 1;
    public static final int NEW = 2;
    public static final int FILTER = 3;
    public static final int FILTER_RESET = 4;
    public static final int SELECT = 5;
    public static final int QUICKVIEW = 6;
    public static final int SEARCH = 7;
    public static final int MENU = 8;
    public static final int DIFF = 9;
    public static final int HELP = 10;
    public static final int SETTINGS = 11;
    public static final int EXIT = 12;
    public static final int NETWORK_DROPBOX = 13;
    public static final int NETWORK_SKYDRIVE = 14;
    public static final int NETWORK_YANDEX = 15;
    public static final int NETWORK = 16;
    public static final int APPLAUNCHER = 17;
    public static final int BOOKMARKS = 18;


    public static final int FILE_ACTION = 100;
    public static final int GAIN_FOCUS = 101;
    public static final int DIRECTORY_CHANGED = 102;
    public static final int FILE_OPEN = 103;
    public static final int OPEN_WITH = 104;
    public static final int OPEN_ARCHIVE = 105;
    public static final int OPEN_COMPRESSED_ARCHIVE = 106;
    public static final int EXIT_FROM_ARCHIVE = 107;
    public static final int EXTRACT_ARCHIVE = 109;
    public static final int CHANGE_PATH = 110;
    public static final int CREATE_BOOKMARK = 111;
    public static final int OPEN_NETWORK = 112;
    public static final int EXIT_FROM_NETWORK_STORAGE = 113;
    public static final int FTP_CONNECTED = 114;
    public static final int SMB_CONNECTED = 115;
    public static final int YANDEX_DISK_USERNAME_RECEIVED = 117;
    public static final int EXIT_FROM_GENERIC_PANEL = 118;
    public static final int SMB_SCAN_NETWORK_REQUESTED = 119;
    public static final int SMB_SCAN_CANCELED = 120;
    public static final int SMB_IP_SELECTED = 121;
    public static final int EXPAND_PANEL = 122;
    public static final int OPEN_PATH = 123;
    public static final int EXPORT_AS = 124;
    public static final int OPEN_WEB = 125;
    public static final int OPEN_ENCODING_DIALOG = 126;
    public static final int MEDIA_FIRE_CONNECTED = 127;
    public static final int SFTP_CONNECTED = 128;
    public static final int WEBDAV_CONNECTED = 129;
    public static final int YANDEX_DISK_CONNECTED = 130;
    public static final int ADD_STAR = 131;
    public static final int REMOVE_STAR = 132;
    public static final int SHARE = 133;
    public static final int GOTO_HOME = 134;
    public static final int OPEN_DIRECTORY = 135;
    public static final int INVALIDATE = 136;
    public static final int SELECT_ALL = 137;
    public static final int UNSELECT_ALL = 138;

    public static final int ARG_FORCE_OPEN_FILE_IN_EDITOR = 1000;
    public static final int ARG_EXPAND_LEFT_PANEL = 1001;
    public static final int ARG_EXPAND_RIGHT_PANEL = 1002;

    protected MainPanel mLeftPanel;
    protected MainPanel mRightPanel;
    protected MainToolbar mToolbar;
    protected ArchivePanel mLeftArchivePanel;
    protected ArchivePanel mRightArchivePanel;
    protected NetworkPanel mLeftNetworkPanel;
    protected NetworkPanel mRightNetworkPanel;
    protected GenericPanel mLeftGenericPanel;
    protected GenericPanel mRightGenericPanel;

    protected DirectoryDetailsView mDirectoryDetailsView;

    protected BasePanel mHiddenPanel;

    protected BasePanel mLeftVisibleFragment;
    protected BasePanel mRightVisibleFragment;

    protected View mLeftFragmentContainer;
    protected View mRightFragmentContainer;

    private Dialog mProgressDialog;

    private PanelsState mPanelsState = PanelsState.EQUALS;

    protected View mMainView;

    protected FileSystemController() {
    }

    public FileSystemController(FragmentManager manager, View view) {

        Context appContext = App.sInstance.getApplicationContext();

        mMainView = view;
        mLeftPanel = (MainPanel) Fragment.instantiate(appContext, MainPanel.class.getName());
        mRightPanel = (MainPanel) Fragment.instantiate(appContext, MainPanel.class.getName());
        mLeftArchivePanel = (ArchivePanel) Fragment.instantiate(appContext, ArchivePanel.class.getName());
        mRightArchivePanel = (ArchivePanel) Fragment.instantiate(appContext, ArchivePanel.class.getName());
        mLeftNetworkPanel = (NetworkPanel) Fragment.instantiate(appContext, NetworkPanel.class.getName());
        mRightNetworkPanel = (NetworkPanel) Fragment.instantiate(appContext, NetworkPanel.class.getName());
        mLeftGenericPanel = (GenericPanel) Fragment.instantiate(appContext, GenericPanel.class.getName());
        mRightGenericPanel = (GenericPanel) Fragment.instantiate(appContext, GenericPanel.class.getName());

        mToolbar = (MainToolbar) mMainView.findViewById(R.id.toolbar);
        mLeftFragmentContainer = view.findViewById(R.id.panel_left);
        mRightFragmentContainer = view.findViewById(R.id.panel_right);

        mDirectoryDetailsView = (DirectoryDetailsView) Fragment.instantiate(appContext, DirectoryDetailsView.class.getName());

        mLeftVisibleFragment = mLeftPanel;
        mRightVisibleFragment = mRightPanel;

        initPanels();

        manager.beginTransaction().add(R.id.panel_left, mLeftPanel).
                add(R.id.panel_right, mRightPanel).
                commit();
    }

    public void invalidateToolbar() {
        mToolbar.invalidate();
    }

    public void hideMainToolbar() {
        mToolbar.setVisibility(View.GONE);
    }

    public void showMainToolbar() {
        mToolbar.setVisibility(View.VISIBLE);
    }

    protected void initPanels() {

        mToolbar.setHandler(mToolbarHandler);

        mLeftPanel.setHandler(mPanelHandler);
        mRightPanel.setHandler(mPanelHandler);

        mLeftArchivePanel.setHandler(mPanelHandler);
        mRightArchivePanel.setHandler(mPanelHandler);

        mLeftNetworkPanel.setHandler(mPanelHandler);
        mRightNetworkPanel.setHandler(mPanelHandler);

        mLeftGenericPanel.setHandler(mPanelHandler);
        mRightGenericPanel.setHandler(mPanelHandler);


        Bundle argsLeft = new Bundle();
        argsLeft.putInt(ARG_PANEL_LOCATION, LEFT_PANEL);
        Bundle argsRight = new Bundle();
        argsRight.putInt(ARG_PANEL_LOCATION, RIGHT_PANEL);

        mLeftPanel.setArguments(argsLeft);
        mLeftArchivePanel.setArguments(argsLeft);
        mLeftNetworkPanel.setArguments(argsLeft);
        mLeftGenericPanel.setArguments(argsLeft);

        mRightPanel.setArguments(argsRight);
        mRightArchivePanel.setArguments(argsRight);
        mRightNetworkPanel.setArguments(argsRight);
        mRightGenericPanel.setArguments(argsRight);
    }

    public void invalidate() {
        MainPanel leftPanel = getLeftVisiblePanel();
        MainPanel rightPanel = getRightVisiblePanel();

        if (leftPanel != null) {
            leftPanel.getSelectedFiles().clear();
            leftPanel.invalidate();
        }

        if (rightPanel != null) {
            rightPanel.getSelectedFiles().clear();
            rightPanel.invalidate();
        }
    }

    public Handler getToolbarHandler() {
        return mToolbarHandler;
    }

    private Handler mToolbarHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            final MainPanel activePanel = getActivePanel();
            final MainPanel inactivePanel = getInactivePanel();

            // something really bad :( but we want to avoid crash, so let's try to make soft restart.
            if (activePanel == null) {
                restorePanelState();
            }

            Activity activity = null;
            if (activePanel != null) {
                activity = activePanel.getActivity();
            }
            if (activity == null || inactivePanel != null) {
                activity = inactivePanel.getActivity();
            }

            switch (msg.what) {
                case ALT_DOWN:
                    if (SystemUtils.isHoneycombOrNever() && !App.sInstance.getSettings().isHoldAltOnTouch()) {
                        if (getLeftVisiblePanel() != null) {
                            getLeftVisiblePanel().setIsMultiSelectMode(true);
                        }
                        if (getRightVisiblePanel() != null) {
                            //getRightVisiblePanel().showAltTip(true);
                            getRightVisiblePanel().setIsMultiSelectMode(true);
                        }
                    } else {
                        if (getLeftVisiblePanel() != null) {
                            getLeftVisiblePanel().switchMultiSelectMode();
                        }
                        if (getRightVisiblePanel() != null) {
                            getRightVisiblePanel().switchMultiSelectMode();
                            //getRightVisiblePanel().showAltTip(getRightVisiblePanel().switchMultiSelectMode());
                        }
                    }
                    break;
                case ALT_UP:
                    if (getLeftVisiblePanel() != null) {
                        getLeftVisiblePanel().setIsMultiSelectMode(false);
                    }
                    if (getRightVisiblePanel() != null) {
                        //getRightVisiblePanel().showAltTip(false);
                        getRightVisiblePanel().setIsMultiSelectMode(false);
                    }
                    break;
                case NEW:
                    if (activePanel != null) {
                        activePanel.createFile(inactivePanel);
                    }
                    break;
                case QUICKVIEW:
                    openQuickPanel(activePanel, inactivePanel);
                    break;
                case EXIT:
                    if (activity != null && !activity.isFinishing()) {
                        activity.finish();
                    }
                    break;
                case FILTER:
                    filter((String) msg.obj);
                    break;
                case SELECT:
                    if (activePanel == null) {
                        return;
                    }
                    activePanel.showSelectDialog();
                    break;
                case SEARCH:
                    if (activePanel == null) {
                        return;
                    }
                    activePanel.showSearchDialog();
                    break;
                case MENU:
                    if (activePanel == null) {
                        return;
                    }
                    activePanel.openFileActionMenu();
                    break;
                case DIFF:
                    diffDirectories();
                    break;
                case HELP:
                    if (activity != null) {
                        activity.startActivity(new Intent(App.sInstance.getApplicationContext(), Help.class));
                    }
                    break;
                case SETTINGS:
                    if (activity != null) {
                        activity.startActivityForResult(new Intent(App.sInstance.getApplicationContext(), SettingsActivity.class), 0);
                    }
                    break;
                case NETWORK:
                    App.sInstance.getNetworkConnectionManager().openAvailableCloudsList();
                    break;
                case APPLAUNCHER:
                    openAppLaucnher();
                    break;
                case BOOKMARKS:
                    if (activePanel != null) {
                        openBookmarkList(activity);
                    }
                    break;
            }

        }

    };

    private void openQuickPanel(MainPanel activePanel, MainPanel inactivePanel) {
        if (!isDetailsPanelVisible()) {
            boolean panelShowed = showDetailsView(activePanel, inactivePanel);
            if (activePanel == null) {
                return;
            }

            if (panelShowed) {
                mDirectoryDetailsView.selectFile(activePanel.getCurrentDir());
            }
        } else {
            hideDetailsView(activePanel, inactivePanel);
        }
    }

    public Handler getPanelHandler() {
        return mPanelHandler;
    }

    private Handler mPanelHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            boolean initPanels = msg.what != GAIN_FOCUS;

            MainPanel activePanel = null;
            MainPanel inactivePanel = null;

            if (initPanels) {
                activePanel = getActivePanel();
                inactivePanel = getInactivePanel();

                if (activePanel != null) {
                    boolean isLeftPanelActive = activePanel.getPanelLocation() == LEFT_PANEL;
                    inactivePanel = isLeftPanelActive ? getRightVisiblePanel() : getLeftVisiblePanel();

                    if (inactivePanel == null) {
                        inactivePanel = getInactivePanel();
                    }
                }
            }

            switch (msg.what) {
                case FILE_ACTION:
                    if (activePanel != null) {
                        activePanel.executeAction((FileActionEnum) msg.obj, inactivePanel);
                    }
                    break;
                case GAIN_FOCUS:
                    BasePanel panel = msg.arg1 == LEFT_PANEL ? mLeftVisibleFragment : mRightVisibleFragment;
                    if (panel instanceof MainPanel) {
                        MainPanel mainPanel = (MainPanel) panel;
                        if (!mainPanel.isActive()) {
                            setActivePanel(mainPanel);
                        }
                    }
                    break;
                case DIRECTORY_CHANGED:
                    if (isDetailsPanelVisible()) {
                        showDetails(activePanel);
                    }
                    break;
                case FILE_OPEN:
                    try {
                        boolean forceOpenEditor = msg.arg1 == ARG_FORCE_OPEN_FILE_IN_EDITOR;
                        openFile((File) msg.obj, forceOpenEditor);
                    } catch (Exception e) {
                        handleUnexpectedException(mLeftVisibleFragment.getActivity());
                    }
                    break;
                case OPEN_WITH:
                    openWith((File) msg.obj);
                    break;
                case OPEN_ARCHIVE:
                    if (activePanel != null) {
                        openArchive(activePanel, (File) msg.obj);
                    }
                    break;
                case OPEN_COMPRESSED_ARCHIVE:
                    if (activePanel != null) {
                        openCompressedArchive(activePanel, (File) msg.obj);
                    }
                    break;
                case EXIT_FROM_ARCHIVE:
                    try {
                        exitFromArchive(getWorkingArchivePanel());
                    } catch (Exception e) {
                        handleUnexpectedException(mLeftVisibleFragment.getActivity());
                    }
                    break;
                case EXTRACT_ARCHIVE:
                    try {
                        getWorkingArchivePanel().extractArchive(inactivePanel);
                    } catch (Exception e) {
                        handleUnexpectedException(mLeftVisibleFragment.getActivity());
                    }
                    break;
                case CHANGE_PATH:
                    changePath((Integer) msg.obj);
                    break;
                case CREATE_BOOKMARK:
                    if (activePanel != null) {
                        activePanel.createBookmark(inactivePanel);
                    }
                    break;
                case OPEN_NETWORK:
                    Bookmark bookmark = (Bookmark) msg.obj;
                    NetworkConnectionManager manager = App.sInstance.getNetworkConnectionManager();
                    if (bookmark != null) {
                        manager.openNetworkBookmark(bookmark);
                    } else {
                        manager.openAvailableCloudsList();
                    }
                    break;
                case EXIT_FROM_NETWORK_STORAGE:
                    exitFromNetworkStorage((Integer) msg.obj == MainPanel.LEFT_PANEL ? mLeftNetworkPanel : mRightNetworkPanel);
                    break;
                case EXIT_FROM_GENERIC_PANEL:
                    exitFromGenericPanel((Integer) msg.obj == MainPanel.LEFT_PANEL ? mLeftGenericPanel : mRightGenericPanel);
                    break;
                case EXPAND_PANEL:
                    try {
                        expandPanel(msg.arg1 == ARG_EXPAND_LEFT_PANEL);
                    } catch (Exception ignore) {
                        // avoid very unexpected crash
                    }
                    break;
                case OPEN_PATH:
                    if (activePanel != null) {
                        activePanel.openDirectory(new File((String) msg.obj));
                    }
                    break;
                case EXPORT_AS:
                    try {
                        openExportAsDialog((GoogleDriveFile) msg.obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case OPEN_WEB:
                    GoogleDriveFile file = (GoogleDriveFile) msg.obj;
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(file.getOpenWithLink()));
                        activePanel.getActivity().startActivity(browserIntent);
                    } catch (ActivityNotFoundException e) {
                        ToastNotification.makeText(activePanel.getActivity(), App.sInstance.getString(R.string.error_no_browser),  Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    break;
                case OPEN_ENCODING_DIALOG:
                    showSelectEncodingDialog((NetworkEnum) msg.obj);
                    break;
                case EditViewController.MSG_SELECT_ENCODING:
                    SelectEncodingDialog.SelectedEncodingInfo info = (SelectEncodingDialog.SelectedEncodingInfo) msg.obj;

                    if (info.networkType == NetworkEnum.SFTP) {
                        App.sInstance.getSftpApi().setCharset(info.charset);
                    } else {
                        App.sInstance.getFtpApi().setCharset(info.charset);
                    }
                    activePanel.invalidatePanels(inactivePanel);
                    break;
                case ADD_STAR:
                    GoogleDriveFile googleDriveFile = (GoogleDriveFile) msg.obj;
                    inactivePanel.updateGoogleDriveData(activePanel, googleDriveFile.getId(), getStarredData(true));
                    break;
                case REMOVE_STAR:
                    googleDriveFile = (GoogleDriveFile) msg.obj;
                    inactivePanel.updateGoogleDriveData(activePanel, googleDriveFile.getId(), getStarredData(false));
                    break;
                case SHARE:
                    activePanel.doDropboxTask((DropboxFile) msg.obj, DropboxTask.TASK_SHARE);
                    break;
                case GOTO_HOME:
                    activePanel.openHomeFolder();
                    break;
                case OPEN_DIRECTORY:
                    activePanel.openDirectory((String) msg.obj);
                    break;
                case INVALIDATE:
                    invalidate();
                    break;
                case SELECT_ALL:
                    activePanel.selectAll();
                    activePanel.invalidate(false);
                    break;
                case UNSELECT_ALL:
                    activePanel.unselectAll();
                    activePanel.invalidate(false);
                    break;
            }
        }

        private String getStarredData(boolean star) {
            return String.format("{\"labels\":{\"starred\":%s}}", star);
        }
    };

    public void expandPanel(boolean expandLeftPanel) {
        ExpandPanelAnimation animation = null;

        switch (mPanelsState) {
            case LEFT_EXPANDED:
                if (!expandLeftPanel) {
                    animation = new ExpandPanelAnimation(mLeftFragmentContainer, mRightFragmentContainer, 1f, 1f);
                    mPanelsState = PanelsState.EQUALS;
                }
                break;
            case EQUALS:
                animation = new ExpandPanelAnimation(mLeftFragmentContainer, mRightFragmentContainer,
                        expandLeftPanel ? 0.8f : 0.2f, expandLeftPanel ? 0.2f : 0.8f);
                mPanelsState = expandLeftPanel ? PanelsState.LEFT_EXPANDED : PanelsState.RIGHT_EXPANDED;

                break;
            case RIGHT_EXPANDED:
                if (expandLeftPanel) {
                    animation = new ExpandPanelAnimation(mLeftFragmentContainer, mRightFragmentContainer, 1f, 1f);
                    mPanelsState = PanelsState.EQUALS;
                }
        }

        if (animation == null) {
            return;
        }

        animation.setDuration(500);
        animation.setFillAfter(true);

        mLeftFragmentContainer.startAnimation(animation);
    }

    private void handleUnexpectedException(Activity activity) {
        // something really unexpected happened. at least avoid crash
        if (activity != null) {
            ToastNotification.makeText(activity, activity.getString(R.string.error_unknown_unexpected_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void changePath(int panel) {
        MainPanel srcPanel = panel == LEFT_PANEL ? mLeftPanel : mRightPanel;
        MainPanel dstPanel = panel == LEFT_PANEL ? mRightPanel : mLeftPanel;

        File path = srcPanel.getCurrentDir();

        if (!dstPanel.isAdded()) {
            int location = dstPanel.getPanelLocation();
            BasePanel activeDstPanel = location == LEFT_PANEL ? mLeftVisibleFragment : mRightVisibleFragment;
            if (activeDstPanel instanceof DirectoryDetailsView) {
                mToolbarHandler.sendEmptyMessage(QUICKVIEW);
            } else if (activeDstPanel instanceof ArchivePanel) {
                ((ArchivePanel) activeDstPanel).exitFromArchive();
            } else if (activeDstPanel instanceof NetworkPanel) {
                ((NetworkPanel) activeDstPanel).exitFromNetwork();
            }
        }

        dstPanel.openDirectory(path);
        dstPanel.gainFocus();
    }

    public void setInitActivePanel() {
        setActivePanel(mLeftPanel);
    }

    protected void setActivePanel(MainPanel activePanel) {

        // remove focus from all panels
        mLeftPanel.setIsActivePanel(false);
        mRightPanel.setIsActivePanel(false);
        mLeftArchivePanel.setIsActivePanel(false);
        mRightArchivePanel.setIsActivePanel(false);
        mLeftNetworkPanel.setIsActivePanel(false);
        mRightNetworkPanel.setIsActivePanel(false);
        mLeftGenericPanel.setIsActivePanel(false);
        mRightGenericPanel.setIsActivePanel(false);

        activePanel.setIsActivePanel(true);
    }

    protected boolean showDetailsView(MainPanel panel, MainPanel inactivePanel) {
        if (panel == null || !mLeftVisibleFragment.isFileSystemPanel() || !mRightVisibleFragment.isFileSystemPanel()) {
            return false;
        }

        boolean isLeftPanel = panel.getPanelLocation() == LEFT_PANEL;
        if (isLeftPanel) {
            mHiddenPanel = mRightVisibleFragment;
            mRightVisibleFragment = mDirectoryDetailsView;
        } else {
            mHiddenPanel = mLeftVisibleFragment;
            mLeftVisibleFragment = mDirectoryDetailsView;
        }

        mHiddenPanel.getFragmentManager().beginTransaction().remove(mHiddenPanel).
                add(getContainerId(!isLeftPanel), mDirectoryDetailsView).commit();

        return true;
    }

    protected void hideDetailsView(MainPanel panel, MainPanel inactivePanel) {
        BasePanel panelToShow = mHiddenPanel;
        boolean isLeftPanel = panel != null ?
                panel.getPanelLocation() == LEFT_PANEL :
                inactivePanel.getPanelLocation() == LEFT_PANEL;

        if (panelToShow == null) {
            panelToShow = isLeftPanel ? mRightVisibleFragment : mLeftVisibleFragment;
        }

        if (isLeftPanel) {
            mRightVisibleFragment = panelToShow;
        } else {
            mLeftVisibleFragment = panelToShow;
        }

        mDirectoryDetailsView.getFragmentManager().beginTransaction().remove(mDirectoryDetailsView).add(getContainerId(!isLeftPanel), panelToShow).commit();
    }

    protected void showDetails(MainPanel panel) {
        if (panel == null || !mDirectoryDetailsView.isAdded()) {
            return;
        }
        mDirectoryDetailsView.selectFile(panel.getCurrentDir());
    }

    protected void openArchive(MainPanel activePanel, File file) {
        ArchivePanel activeArchivePanel = switchToArchivePanel(activePanel);
        activeArchivePanel.openArchive(file);
    }

    protected void openCompressedArchive(MainPanel activePanel, File file) {
        ArchivePanel activeArchivePanel = switchToArchivePanel(activePanel);
        activeArchivePanel.openCompressedArchive(file);
    }

    private ArchivePanel switchToArchivePanel(MainPanel activePanel) {
        boolean isLeftPanel = activePanel.getPanelLocation() == MainPanel.LEFT_PANEL;
        ArchivePanel activeArchivePanel;
        FragmentManager fragmentManager;

        if (isLeftPanel) {
            activeArchivePanel = mLeftArchivePanel;
            mLeftVisibleFragment = mLeftArchivePanel;
            fragmentManager = mRightVisibleFragment.getFragmentManager();
        } else {
            activeArchivePanel = mRightArchivePanel;
            mRightVisibleFragment = mRightArchivePanel;
            fragmentManager = mLeftVisibleFragment.getFragmentManager();
        }

        activeArchivePanel.gainFocus();
        fragmentManager.beginTransaction().remove(activePanel).add(getContainerId(isLeftPanel), activeArchivePanel).commit();
        return activeArchivePanel;
    }

    public void openNetworkPanel(NetworkEnum networkType) {
        openNetworkPanel(networkType, null);
    }

    public void openNetworkPanel(NetworkEnum networkType, String path) {
        MainPanel activePanel = getActivePanel();
        boolean isLeftPanel = activePanel.getPanelLocation() == MainPanel.LEFT_PANEL;
        NetworkPanel networkPanel;
        FragmentManager fragmentManager;

        forceExitFromNetwork(networkType, activePanel);
        if (isLeftPanel) {
            networkPanel = mLeftNetworkPanel;
            mLeftVisibleFragment = networkPanel;
            fragmentManager = mRightVisibleFragment.getFragmentManager();
        } else {
            networkPanel = mRightNetworkPanel;
            mRightVisibleFragment = networkPanel;
            fragmentManager = mLeftVisibleFragment.getFragmentManager();
        }

        networkPanel.setNetworkType(networkType);
        networkPanel.gainFocus();
        fragmentManager.beginTransaction().remove(activePanel).add(getContainerId(isLeftPanel), networkPanel).commit();
        networkPanel.openBookmark(path);
    }

    protected void forceExitFromNetwork(NetworkEnum networkType, MainPanel activePanel) {
        MainPanel inactivePanel = getInactivePanel();
        if (inactivePanel instanceof NetworkPanel) {
            final NetworkPanel inactiveNetworkPanel = (NetworkPanel) inactivePanel;
            NetworkAccount newAccount = App.sInstance.getNetworkApi(networkType).getCurrentNetworkAccount();
            NetworkAccount account = inactiveNetworkPanel.getCurrentNetworkAccount();

            if (account != null && newAccount.getId() == account.getId()) {
                return;
            }

            if (inactiveNetworkPanel.getNetworkType() == networkType) {
                activePanel.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastNotification.makeText(App.sInstance.getApplicationContext(),
                                App.sInstance.getString(R.string.one_network_instance), Toast.LENGTH_LONG).show();
                        inactiveNetworkPanel.exitFromNetwork();
                    }
                });
            }
        }
    }

    protected void exitFromArchive(ArchivePanel archivePanel) {
        boolean isLeftPanel = archivePanel.getPanelLocation() == MainPanel.LEFT_PANEL;
        MainPanel activePanel = isLeftPanel ? mLeftPanel : mRightPanel;
        FragmentManager fragmentManager;

        if (isLeftPanel) {
            mLeftVisibleFragment = activePanel;
            fragmentManager = mRightVisibleFragment.getFragmentManager();
        } else {
            mRightVisibleFragment = activePanel;
            fragmentManager = mLeftVisibleFragment.getFragmentManager();
        }
        activePanel.gainFocus();
        fragmentManager.beginTransaction().remove(archivePanel).add(getContainerId(isLeftPanel), activePanel).commit();
    }

    protected void exitFromGenericPanel(MainPanel genericPanel) {
        boolean isLeftPanel = genericPanel.getPanelLocation() == MainPanel.LEFT_PANEL;
        MainPanel activePanel = isLeftPanel ? mLeftPanel : mRightPanel;
        FragmentManager fragmentManager;

        if (isLeftPanel) {
            mLeftVisibleFragment = activePanel;
            fragmentManager = mRightVisibleFragment.getFragmentManager();
        } else {
            mRightVisibleFragment = activePanel;
            fragmentManager = mLeftVisibleFragment.getFragmentManager();
        }
        activePanel.gainFocus();
        fragmentManager.beginTransaction().remove(genericPanel).add(getContainerId(isLeftPanel), activePanel).commit();
    } 
    
    protected void exitFromNetworkStorage(NetworkPanel networkPanel) {
        boolean isLeftPanel = networkPanel.getPanelLocation() == MainPanel.LEFT_PANEL;
        MainPanel activePanel = isLeftPanel ? mLeftPanel : mRightPanel;
        FragmentManager fragmentManager;

        if (isLeftPanel) {
            mLeftVisibleFragment = activePanel;
            fragmentManager = mRightVisibleFragment.getFragmentManager();
        } else {
            mRightVisibleFragment = activePanel;
            fragmentManager = mLeftVisibleFragment.getFragmentManager();
        }
        activePanel.gainFocus();
        fragmentManager.beginTransaction().remove(networkPanel).add(getContainerId(isLeftPanel), activePanel).commit();
    }

    private int getContainerId(boolean isLeftPanel) {
        return isLeftPanel ? R.id.panel_left : R.id.panel_right;
    }

    private void filter(String obj) {
        if (getActivePanel() != null) {
            getActivePanel().filter(obj);
        }
    }

    private void diffDirectories() {
        final MainPanel activePanel = getActivePanel();
        final MainPanel inactivePanel = getInactivePanel();

        if (activePanel == null || inactivePanel == null) {
            return;
        }

        if (!activePanel.isFileSystemPanel() || !inactivePanel.isFileSystemPanel()) {
            Activity activity = activePanel.getActivity();
            ToastNotification.makeText(activity, activity.getString(R.string.error_diff_not_supported), Toast.LENGTH_SHORT).show();
            return;
        }

        final File activePanelCurrentDir = activePanel.getCurrentDir();
        final File inactivePanelCurrentDir = inactivePanel.getCurrentDir();

        final DiffDirectoriesTask task = new DiffDirectoriesTask(new DiffDirectoriesTask.OnActionListener() {
            @Override
            public void onActionFinish(LinkedHashSet<File> activePanelDiffFiles, LinkedHashSet<File> inactivePanelDiffFiles) {
                if (!activePanelCurrentDir.equals(activePanel.getCurrentDir()) ||
                        !inactivePanelCurrentDir.equals(inactivePanel.getCurrentDir())) {
                    return;
                }
                activePanel.addSelectedFiles(activePanelDiffFiles);
                inactivePanel.addSelectedFiles(inactivePanelDiffFiles);
            }
        }, getActivePanel().getAllFiles(), getInactivePanel().getAllFiles());
        task.execute();
    }

    public void savePanelState() {
        App.sInstance.getSettings().savePanelsState(mLeftPanel.getCurrentDir().getAbsolutePath(),
                mRightPanel.getCurrentDir().getAbsolutePath(), getActivePanel() == mLeftPanel);
    }

    public void restorePanelState() {
        Settings settings = App.sInstance.getSettings();
        mLeftPanel.restoreState(settings.getLeftPanelPath(), settings.isLeftPanelActive());
        mRightPanel.restoreState(settings.getRightPanelPath(), !settings.isLeftPanelActive());
    }

    private MainPanel getVisibleActivePanel() {
        MainPanel panel = getActivePanel();
        return panel != null && panel.isAdded() ? panel : getWorkingArchivePanel();
    }

    public MainPanel getActivePanel() {
        MainPanel leftVisiblePanel = getLeftVisiblePanel();
        MainPanel rightVisiblePanel = getRightVisiblePanel();

        if (leftVisiblePanel != null && leftVisiblePanel.isActive()) {
            return leftVisiblePanel;
        } else if (rightVisiblePanel != null && rightVisiblePanel.isActive()) {
            return rightVisiblePanel;
        } else {
            // hack: avoid NPE: request focus for non null panel.
            if (leftVisiblePanel != null) {
                leftVisiblePanel.gainFocus();
                return leftVisiblePanel;
            } else if (rightVisiblePanel != null) {
                rightVisiblePanel.gainFocus();
                return rightVisiblePanel;
            }

            return null;
        }
    }

    protected ArchivePanel getWorkingArchivePanel() {
        if (mLeftArchivePanel.isActive()) {
            return mLeftArchivePanel;
        } else if (mRightArchivePanel.isActive()) {
            return mRightArchivePanel;
        } else {
            if (mLeftVisibleFragment instanceof ArchivePanel) {
                return (ArchivePanel) mLeftVisibleFragment;
            } else if (mRightVisibleFragment instanceof ArchivePanel) {
                return (ArchivePanel) mRightVisibleFragment;
            }

            return null;
        }
    }

    public MainPanel getInactivePanel() {
        MainPanel leftVisiblePanel = getLeftVisiblePanel();
        MainPanel rightVisiblePanel = getRightVisiblePanel();

        if (leftVisiblePanel != null && !leftVisiblePanel.isActive()) {
            return leftVisiblePanel;
        } else if (rightVisiblePanel != null && !rightVisiblePanel.isActive()) {
            return rightVisiblePanel;
        } else {
            return null;
        }
    }

    protected MainPanel getLeftVisiblePanel() {
        return mLeftVisibleFragment instanceof MainPanel ? (MainPanel) mLeftVisibleFragment : null;
    }

    protected MainPanel getRightVisiblePanel() {
        return mRightVisibleFragment instanceof MainPanel ? (MainPanel) mRightVisibleFragment : null;
    }

    protected boolean isDetailsPanelVisible() {
        return mDirectoryDetailsView.isAdded();
    }

    public boolean navigateBack() {
        MainPanel panel = getVisibleActivePanel();
        if (panel != null) {
            if (panel.isRootDirectory()) {
                if (panel instanceof ArchivePanel) {
                    ((ArchivePanel) panel).exitFromArchive();
                    return true;
                } else if (panel instanceof NetworkPanel) {
                    panel.navigateParent();
                    return true;
                } else if (panel instanceof GenericPanel) {
                    panel.navigateParent();
                    return true;
                }

                return false;
            } else {
                panel.unselectAll();
                panel.navigateParent();
                return true;
            }
        }
        return false;
    }

    private void openFile(File item, boolean forceOpenEditor) {
        MainPanel panel = getActivePanel();

        if (panel == null) {
            panel = getInactivePanel();
        }

        // unexpected error, it's better to just exit from method.
        if (panel == null || item == null) {
            return;
        }

        Activity activity = panel.getActivity();

        // unexpected error, it's better to just exit from method.
        if (activity == null) {
            return;
        }

        if (!item.canRead() && !RootTask.canReadFile(item)) {
            ToastNotification.makeText(activity, activity.getString(R.string.error_cannot_open_file), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!forceOpenEditor && openExternal(item)) {
            return;
        }

        Intent intent;
        //Can't find application, user internal viewer
        //try to detect zip archive
        if (!forceOpenEditor) {
            try {
                FileInputStream is = new FileInputStream(item);
                byte[] buff = new byte[2];
                is.read(buff);
                if (buff[0] == 0x50 && buff[1] == 0x4b) {
                    //ToastNotification.makeText(activity,"Zip archive",Toast.LENGTH_SHORT).show();
                    openArchive(getActivePanel(), item);
                    return;
                }
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Can't find application, user internal viewer
        intent = new Intent(App.sInstance, FileView.class).setData(Uri.fromFile(item));
        activity.startActivity(intent);
    }

    /**
     * Try to open file by external application.
     *
     * @param item file to open
     * @return <code>true</code> application to handle this file was found, <code>false</code> otherwise.
     */
    private boolean openExternal(File item) {
        Intent intent = App.sInstance.getAppManager().getFilteredIntent(item);

        String mime = intent.getType();
        //try to find external viewer
        try {
            if (mime != null && !mime.startsWith("text")) {
                if (intent.resolveActivity(App.sInstance.getPackageManager()) != null) {
                    getActivePanel().getActivity().startActivity(intent);
                    return true;
                }
            }
        } catch (Exception ignore) { /*sometimes unexpected NPE occurs */}

        return false;
    }

    /**
     * Perform 'Open width' operation - chow chooser dialog with applications to handle selected file.
     *
     * @param item to handle.
     */
    private void openWith(File item) {
        final Intent intent = App.sInstance.getAppManager().getFilteredIntent(item);
        List<ResolveInfo> infoList = App.sInstance.getAppManager().getIntentActivities(intent);
        List<ResolveInfo> allInfoList = App.sInstance.getAppManager().getAllCallableActivities(item);
        showChooserDialog(intent, item, infoList, allInfoList);
    }

    public void openBookmarkList(Activity activity) {
        final Dialog dialog = new BookmarksListDialog(activity, mPanelHandler);
        dialog.show();
        adjustDialogSize(dialog);
    }

    /**
     * Show customized chooser dialog with available applications for current <code>intent</code>.
     *
     * @param intent          intent to be handled. Created from previously selected file.
     * @param itemToHandle    file item to be opened.
     * @param applications    available apps which can handle requested intent.
     * @param allApplications all applications which can handle any content.
     */
    private void showChooserDialog(final Intent intent, final File itemToHandle, List<ResolveInfo> applications, List<ResolveInfo> allApplications) {
        final Dialog dialog = new Dialog(getActivePanel().getActivity(), R.style.Action_Dialog_Invert);
        View dialogView = View.inflate(App.sInstance.getApplicationContext(), R.layout.application_chooser, null);

        final ListView appList = (ListView) dialogView.findViewById(R.id.applications_list);
        final ApplicationChooserAdapter adapter = new ApplicationChooserAdapter(applications, allApplications);
        appList.setAdapter(adapter);
        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean isDelimiter = (Boolean) view.getTag(R.string.is_delimiter);
                if (isDelimiter) {
                    adapter.delimiterClicked();
                    return;
                }

                ResolveInfo info = (ResolveInfo) appList.getAdapter().getItem(position);

                //Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                Activity activity = getActivePanel().getActivity();
                try {
                    activity.startActivity(intent);
                } catch (SecurityException e) {
                    ToastNotification.makeText(activity, activity.getString(R.string.error_cant_open_intent), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.open_as).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                showOpenAsDialog(itemToHandle);
            }
        });

        dialog.setContentView(dialogView);
        dialog.show();

        adjustDialogSize(dialog);
    }

    /**
     * Show mime type chooser dialog.
     *
     * @param itemToHandle file item to be opened.
     */
    private void showOpenAsDialog(final File itemToHandle) {
        final Dialog dialog = new Dialog(getActivePanel().getActivity(), R.style.Action_Dialog_Invert);
        View dialogView = View.inflate(App.sInstance.getApplicationContext(), R.layout.mime_type_chooser, null);

        final ListView mimeTypes = (ListView) dialogView.findViewById(R.id.mime_types);
        final MimeTypeChooserAdapter adapter = new MimeTypeChooserAdapter();
        mimeTypes.setAdapter(adapter);

        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        mimeTypes.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mime = (String) view.getTag();

                final Intent intent = App.sInstance.getAppManager().getFilteredIntent(itemToHandle);
                List<ResolveInfo> infoList = App.sInstance.getAppManager().getCallableActivities(itemToHandle, mime);
                List<ResolveInfo> allInfoList = App.sInstance.getAppManager().getAllCallableActivities(itemToHandle);
                showChooserDialog(intent, itemToHandle, infoList, allInfoList);

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.setContentView(dialogView);
        dialog.show();

        adjustDialogSize(dialog);
    }

    public void showSelectEncodingDialog(NetworkEnum network) {
        Dialog mCharsetSelectDialog = new SelectEncodingDialog(getActivePanel().getActivity(), mPanelHandler, network, false);
        mCharsetSelectDialog.setCancelable(true);
        mCharsetSelectDialog.show();
        adjustDialogSize(mCharsetSelectDialog, 0.6f);
    }

    private void openExportAsDialog(final GoogleDriveFile file) {
        final Dialog dialog = new Dialog(getActivePanel().getActivity(), R.style.Action_Dialog_Invert);
        View dialogView = View.inflate(App.sInstance.getApplicationContext(), R.layout.mime_type_chooser, null);

        final ListView exportTypes = (ListView) dialogView.findViewById(R.id.mime_types);
        final ExportAsAdapter adapter = new ExportAsAdapter(file.getExportLinks());
        exportTypes.setAdapter(adapter);

        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ((TextView) dialogView.findViewById(R.id.title)).setText(App.sInstance.getString(R.string.export_as));
        exportTypes.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String downloadLink = (String) view.getTag();

                final MainPanel inactivePanel = getInactivePanel();

                if (inactivePanel == null || inactivePanel.getActivity().isFinishing()) {
                    return;
                }

                final String fileName = file.getName();

                inactivePanel.export(getActivePanel(), downloadLink,
                        inactivePanel.getCurrentPath() + File.separator + fileName + "." +
                                downloadLink.substring(downloadLink.lastIndexOf('=') + 1));

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.setContentView(dialogView);
        dialog.show();

        adjustDialogSize(dialog);
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
            getActivePanel().getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(dialog.getWindow().getAttributes());
            params.width = (int) (metrics.widthPixels * scaleFactor);
            params.height = (int) (metrics.heightPixels * scaleFactor);

            dialog.getWindow().setAttributes(params);
        }
    }

    public void openAppLaucnher() {
        MainPanel activePanel = getActivePanel();

        if (activePanel == null) {
            return;
        }

        if (activePanel instanceof GenericPanel) {
            exitFromGenericPanel(activePanel);
            return;
        }

        boolean isLeftPanel = activePanel.getPanelLocation() == MainPanel.LEFT_PANEL;
        GenericPanel panel;
        FragmentManager fragmentManager;
        if (isLeftPanel) {
            panel = mLeftGenericPanel;
            mLeftVisibleFragment = panel;
            fragmentManager = mRightVisibleFragment.getFragmentManager();
        } else {
            panel = mRightGenericPanel;
            mRightVisibleFragment = panel;
            fragmentManager = mLeftVisibleFragment.getFragmentManager();
        }
        panel.gainFocus();
        fragmentManager.beginTransaction().remove(activePanel).add(getContainerId(isLeftPanel), panel).commit();
    }

    public void showProgressDialog(int messageId) {
        try {
            getActivePanel().getActivity().runOnUiThread(mShowProgressRunnable);
            ((TextView) mProgressDialog.findViewById(R.id.progress_bar_text)).setText(messageId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissProgressDialog() {
        runOnUiThread(mDismissProgressRunnable);
    }

    private void runOnUiThread(Runnable runnable) {
        try {
            getActivePanel().getActivity().runOnUiThread(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MainPanel panel = getActivePanel();
        MainPanel inactivePanel = getInactivePanel();

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return navigateBack();

            //Hardware keybord events
            case KeyEvent.KEYCODE_TAB:
                MainPanel active = getActivePanel();
                getLeftVisiblePanel().setIsActivePanel(getLeftVisiblePanel() != active);
                getRightVisiblePanel().setIsActivePanel(getRightVisiblePanel() != active);
                return true;
            case KeyEvent.KEYCODE_DEL:
                navigateBack();
                return true;
            case KeyEvent.KEYCODE_F1:
                if (event.isAltPressed()) {
                    App.sInstance.getNetworkConnectionManager().openAvailableCloudsList();
                }
                return true;
            case KeyEvent.KEYCODE_F2:
                if (event.isAltPressed()) {
                    if (panel != null) {
                        openQuickPanel(panel, inactivePanel);
                    }
                } else {
                    getActivePanel().openFileActionMenu();
                }
                return true;
            case KeyEvent.KEYCODE_F3:
                if (event.isAltPressed()) {
                    if (panel != null) {
                        panel.showSearchDialog();
                    }
                } else {
                    openAppLaucnher();
                }
                return true;
            case KeyEvent.KEYCODE_F4:
                if (event.isAltPressed()) {
                    if (panel != null) {
                        Activity activity = panel.getActivity();
                        if (activity != null) {
                            openBookmarkList(activity);
                        }
                    }
                } else {
                    if (panel != null) {
                        panel.showSelectDialog();
                    }
                }
                return true;
            case KeyEvent.KEYCODE_F5:
                getActivePanel().copy(getInactivePanel());
                return true;
            case KeyEvent.KEYCODE_F6:
                getActivePanel().move(getInactivePanel(), false);
                return true;
            case KeyEvent.KEYCODE_F7:
                getActivePanel().createFile(getInactivePanel());
                return true;
            case KeyEvent.KEYCODE_F8:
                getActivePanel().delete(getInactivePanel());
                return true;
            case KeyEvent.KEYCODE_F9:
                getActivePanel().showSearchDialog();
                return true;
            case KeyEvent.KEYCODE_F10:
                if (panel != null) {
                    Activity activity = panel.getActivity();
                    if (activity != null) {
                        activity.startActivity(new Intent(App.sInstance.getApplicationContext(), Help.class));
                    }
                }
                return true;
            case KeyEvent.KEYCODE_F11:
                if (panel != null) {
                    Activity activity = panel.getActivity();
                    if (activity != null) {
                        activity.startActivity(new Intent(App.sInstance.getApplicationContext(), SettingsActivity.class));
                    }
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (event.isShiftPressed()) {
                    getActivePanel().selectCurrentFile(1);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (event.isShiftPressed()) {
                    getActivePanel().selectCurrentFile(-1);
                }
                return true;

        }
        return false;
    }

    private Runnable mDismissProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    };

    private Runnable mShowProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                dismissProgressDialog();
            }

            mProgressDialog = new Dialog(getActivePanel().getActivity(), android.R.style.Theme_Translucent);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setContentView(R.layout.dialog_progress);
            mProgressDialog.show();
        }
    };
}
