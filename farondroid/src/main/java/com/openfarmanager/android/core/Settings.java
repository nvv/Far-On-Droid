package com.openfarmanager.android.core;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.filesystem.FileSystemScanner;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.utils.StorageUtils;
import com.openfarmanager.android.utils.SystemUtils;

public class Settings {

    public static final String PANEL_STATE_SETTINGS = "panelState";
    public static final String LEFT_PANEL_PATH = "left";
    public static final String RIGHT_PANEL_PATH = "right";
    public static final String ACTIVE_PANEL = "active_panel";
    public static final String FTP_HOST_CHARSET = "ftp_host_charset";

    public static final String SDCARD_ROOT = "sdcard_root";
    public static final String HIDE_SYSTEM_FILES = "hide_system_files";
    public static final String FILES_SORT = "files_sort";
    public static final String FILE_INFO = "file_info";
    public static final String FOLDERS_FIRST = "folders_first";
    public static final String MULTI_PANELS = "multi_panels";
    public static final String MULTI_PANELS_CHANGED = "multi_panels_changed";
    public static final String FLEXIBLE_PANELS = "flexible_panels";
    public static final String FORCE_EN_LANG = "force_en_lang";
    public static final String SHOW_TIPS = "show_tips_full_screen_new";
    public static final String SHOW_TOOLBAR_TIPS = "show_toolbar_tips_full_screen_new";
    public static final String ENABLE_HOME_FOLDER = "enable_home_folder";
    public static final String HOME_FOLDER = "home_folder";
    public static final String MAIN_PANEL_FONT_SIZE = "main_panel_font_size";
    public static final String MAIN_PANEL_CELL_MARGIN = "main_panel_cell_margin";
    public static final String BOTTOM_PANEL_FONT_SIZE = "bottom_panel_font_size";
    public static final String VIEWER_FONT_SIZE = "viewer_panel_font_size";
    public static final String VIEWER_DEFAULT_CHARSET_NAME = "viewer_charset_name";
    public static final String ROOT_ENABLED = "root_enabled";
    public static final String MAIN_PANEL_FONT_NAME = "main_panel_font";
    public static final String VIEWER_FONT_NAME = "viewer_panel_font";
    public static final String MAIN_PANEL_COLOR = "main_panel_color";
    public static final String VIEWER_COLOR = "viewer_color";
    public static final String SECONDARY_COLOR = "secondary_color";
    public static final String TEXT_COLOR = "text_color";
    public static final String FOLDER_COLOR = "folder_color";
    public static final String SELECTED_COLOR = "selected_color";
    public static final String HIDDEN_COLOR = "hidden_color";
    public static final String INSTALL_COLOR = "install_color";
    public static final String ARCHIVE_COLOR = "archive_color";
    public static final String SHOW_SELECTED_FILES_SIZE = "show_selected_files_size";
    public static final String HOLD_ALT_BY_CLICK = "hold_alt_by_click";
    public static final String SHOW_QUICK_ACTION_PANEL = "show_quick_action_panel";
    public static final String HIDE_MAIN_TOOLBAR = "hide_main_toolbar";
    public static final String REPLACE_DELIMETERS = "replace_delimeters";
    public static final String MULTI_THREAD_TASKS = "support_multithread_tasks";
    public static final String MULTI_ACTION_LABEL_TYPE = "multi_action_label_type";
    public static final String SDCARD_PERMISSION_ASKED = "sdcard_permission_asked";

    public static final String FTP_ALLOW_RECURSIVE_DELETE = "allow_recursive_delete";
    public static final String FTP_ASK_FOR_PERMISSION = "ask_for_permision";

    public static final int MULTI_ACTION_LABEL_TYPE_FILES_NUM = 0;
    public static final int MULTI_ACTION_LABEL_TYPE_LIST_FILES = 1;

    private int mMainPanelFontSize = 0;
    private int mBottomPanelFontSize = 0;
    private int mViewerFontSize = 0;
    private int mPanelCellMargin = 0;

    private String mMainPanelFont;
    private String mViewerFont;

    private Typeface mMainPanelFontType;
    private Typeface mViewerFontType;

    private int mMainPanelColor = 0;
    private int mViewerColor = 0;
    private int mSecondaryColor = 0;
    private int mTextColor = 0;
    private int mFolderColor = 0;
    private int mHiddenColor = 0;
    private int mInstallColor = 0;
    private int mSelectedColor = 0;
    private int mArchiveColor = 0;

    public void savePanelsState(String leftPanelPath, String rightPanelPath, boolean isLeftPanelActive) {
        SharedPreferences.Editor edit = getPanelSettings().edit();
        edit.putString(LEFT_PANEL_PATH, leftPanelPath);
        edit.putString(RIGHT_PANEL_PATH, rightPanelPath);
        edit.putBoolean(ACTIVE_PANEL, isLeftPanelActive);
        edit.commit();
    }

    public SharedPreferences getHostCharset() {
        return App.sInstance.getSharedPreferences(FTP_HOST_CHARSET, 0);
    }

    public void saveCharset(String host, String charset) {
        getHostCharset().edit().putString(host, charset).commit();
    }

    public String getCharset(String host) {
        return getHostCharset().getString(host, null);
    }

    public void saveSftpCharset(String host, String charset) {
        getHostCharset().edit().putString("sftp_" + host, charset).commit();
    }

    public String getSftpCharset(String host) {
        return getHostCharset().getString("sftp_" + host, null);
    }

    public SharedPreferences getPanelSettings() {
        return App.sInstance.getSharedPreferences(PANEL_STATE_SETTINGS, 0);
    }

    public String getLeftPanelPath() {
        return getPanelSettings().getString(LEFT_PANEL_PATH, StorageUtils.getSdPath());
    }

    public String getRightPanelPath() {
        return getPanelSettings().getString(RIGHT_PANEL_PATH, StorageUtils.getSdPath());
    }

    public boolean isLeftPanelActive() {
        return getPanelSettings().getBoolean(ACTIVE_PANEL, true);
    }

    public SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(App.sInstance);
    }

    public boolean isSDCardRoot() {
        return getSharedPreferences().getBoolean(SDCARD_ROOT, false);
    }

    public boolean isHideMainToolbar() {
        return getSharedPreferences().getBoolean(HIDE_MAIN_TOOLBAR, false);
    }

    public boolean isHideSystemFiles() {
        return getSharedPreferences().getBoolean(HIDE_SYSTEM_FILES, false);
    }

    public boolean isFoldersFirst() {
        return getSharedPreferences().getBoolean(FOLDERS_FIRST, false);
    }

    public String getFileSortValue() {
        return getSharedPreferences().getString(FILES_SORT, "0");
    }

    /**
     * Get file info requested type.
     * <p/>
     * 0 - File Size
     * 1 - Modification Date
     * 2 - Permissions
     *
     * @return file type info string index.
     */
    public String getFileInfoType() {
        return getSharedPreferences().getString(FILE_INFO, "0");
    }

    public void setFileSortValue(String value) {
        getSharedPreferences().edit().putString(Settings.FILES_SORT, value).commit();
        FileSystemScanner.sInstance.initSorters();
    }

    public void setFileInfoTypeValue(String value) {
        getSharedPreferences().edit().putString(Settings.FILE_INFO, value).commit();
    }

    public boolean isMultiPanelMode() {
        if (!getSharedPreferences().getBoolean(Settings.MULTI_PANELS_CHANGED, false)) {
            return SystemUtils.isTablet();
        }

        return getSharedPreferences().getBoolean(MULTI_PANELS, SystemUtils.isTablet());
    }

    public boolean isFlexiblePanelsMode() {
        return getSharedPreferences().getBoolean(FLEXIBLE_PANELS, false);
    }

    public boolean isForceUseEn() {
        return getSharedPreferences().getBoolean(FORCE_EN_LANG, false);
    }

    public boolean isEnableHomeFolder() {
        return getSharedPreferences().getBoolean(ENABLE_HOME_FOLDER, false);
    }

    public String getHomeFolder() {
        return getSharedPreferences().getString(HOME_FOLDER, StorageUtils.getSdPath());
    }

    public void setHomeFolder(String path) {
        getSharedPreferences().edit().putString(HOME_FOLDER, path).commit();
    }

    public int getMainPanelFontSize() {
        if (mMainPanelFontSize == 0) {
            mMainPanelFontSize = getSharedPreferences().getInt(MAIN_PANEL_FONT_SIZE, 18);
        }

        return mMainPanelFontSize;
    }

    public void setMainPanelFontSize(int size) {
        getSharedPreferences().edit().putInt(MAIN_PANEL_FONT_SIZE, size).commit();
        mMainPanelFontSize = size;
    }


    public int getMainPanelCellMargin() {
        if (mPanelCellMargin == 0) {
            mPanelCellMargin = getSharedPreferences().getInt(MAIN_PANEL_CELL_MARGIN, 2);
        }

        return mPanelCellMargin;
    }

    public void setMainPanelCellMargin(int size) {
        getSharedPreferences().edit().putInt(MAIN_PANEL_CELL_MARGIN, size).commit();
        mPanelCellMargin = size;
    }

    public int getBottomPanelFontSize() {
        if (mBottomPanelFontSize == 0) {
            mBottomPanelFontSize = getSharedPreferences().getInt(BOTTOM_PANEL_FONT_SIZE, 14);
        }

        return mBottomPanelFontSize;
    }

    public void setBottomPanelFontSize(int size) {
        getSharedPreferences().edit().putInt(BOTTOM_PANEL_FONT_SIZE, size).commit();
        mBottomPanelFontSize = size;
    }

    public int getViewerFontSize() {
        if (mViewerFontSize == 0) {
            mViewerFontSize = getSharedPreferences().getInt(VIEWER_FONT_SIZE, 14);
        }

        return mViewerFontSize;
    }

    public void setViewerFontSize(int size) {
        getSharedPreferences().edit().putInt(VIEWER_FONT_SIZE, size).commit();
        mViewerFontSize = size;
    }

    public boolean isShowTips() {
        return getSharedPreferences().getBoolean(SHOW_TIPS, true);
    }

    public boolean isShowToolbarTips() {
        return getSharedPreferences().getBoolean(SHOW_TOOLBAR_TIPS, true);
    }

    public void setDefaultCharset(String key) {
        getSharedPreferences().edit().putString(VIEWER_DEFAULT_CHARSET_NAME, key).commit();
    }

    public String getDefaultCharset() {
        return getSharedPreferences().getString(VIEWER_DEFAULT_CHARSET_NAME, "UTF-8");
    }

    public boolean getRootEnabled() {
        return getSharedPreferences().getBoolean(ROOT_ENABLED, false);
    }

    public Typeface getMainPanelFontType() {
        if (mMainPanelFontType == null) {
            setMainPanelFontType();
        }

        return mMainPanelFontType;
    }

    private void setMainPanelFontType() {
        try {
            mMainPanelFontType = Typeface.createFromFile(getMainPanelFont());
        } catch (Exception e) {
            mMainPanelFontType = Typeface.DEFAULT;
        }
    }

    private void setViewerFontType() {
        try {
            mViewerFontType = Typeface.createFromFile(getViewerFont());
        } catch (Exception e) {
            mViewerFontType = Typeface.DEFAULT;
        }
    }

    public Typeface getViewerFontType() {
        if (mViewerFontType == null) {
            setViewerFontType();
        }

        return mViewerFontType;
    }

    public void setMainPanelFont(String font) {
        getSharedPreferences().edit().putString(MAIN_PANEL_FONT_NAME, font).commit();
        mMainPanelFont = font;
        setMainPanelFontType();
    }

    public String getMainPanelFont() {
        if (mMainPanelFont == null) {
            mMainPanelFont = getSharedPreferences().getString(MAIN_PANEL_FONT_NAME, "");
        }

        return mMainPanelFont;
    }

    public void setViewerFont(String font) {
        getSharedPreferences().edit().putString(VIEWER_FONT_NAME, font).commit();
        mViewerFont = font;
        setViewerFontType();
    }

    public String getViewerFont() {
        if (mViewerFont == null) {
            mViewerFont = getSharedPreferences().getString(VIEWER_FONT_NAME, "");
        }

        return mViewerFont;
    }

    public int getMainPanelColor() {
        if (mMainPanelColor == 0) {
            mMainPanelColor = getSharedPreferences().getInt(MAIN_PANEL_COLOR, Color.parseColor(App.sInstance.getString(R.color.main_blue)));
        }

        return mMainPanelColor;
    }

    public void setMainPanelColor(int color) {
        getSharedPreferences().edit().putInt(MAIN_PANEL_COLOR, color).commit();
        mMainPanelColor = color;
    }

    public int getViewerColor() {
        if (mViewerColor == 0) {
            mViewerColor = getSharedPreferences().getInt(VIEWER_COLOR, Color.parseColor(App.sInstance.getString(R.color.main_blue)));
        }

        return mViewerColor;
    }

    public void setViewerColor(int color) {
        getSharedPreferences().edit().putInt(VIEWER_COLOR, color).commit();
        mViewerColor = color;
    }

    public int getSecondaryColor() {
        if (mSecondaryColor == 0) {
            mSecondaryColor = getSharedPreferences().getInt(SECONDARY_COLOR, Color.parseColor(App.sInstance.getString(R.color.selected_item)));
        }

        return mSecondaryColor;
    }

    public void setSecondaryColor(int color) {
        getSharedPreferences().edit().putInt(SECONDARY_COLOR, color).commit();
        mSecondaryColor = color;
    }

    public int getTextColor() {
        if (mTextColor == 0) {
            mTextColor = getSharedPreferences().getInt(TEXT_COLOR, Color.CYAN);
        }

        return mTextColor;
    }

    public void setTextColor(int color) {
        getSharedPreferences().edit().putInt(TEXT_COLOR, color).commit();
        mTextColor = color;
    }

    public int getFolderColor() {
        if (mFolderColor == 0) {
            mFolderColor = getSharedPreferences().getInt(FOLDER_COLOR, Color.WHITE);
        }

        return mFolderColor;
    }

    public void setFolderColor(int color) {
        getSharedPreferences().edit().putInt(FOLDER_COLOR, color).commit();
        mFolderColor = color;
    }

    public int getSelectedColor() {
        if (mSelectedColor == 0) {
            mSelectedColor = getSharedPreferences().getInt(SELECTED_COLOR, Color.YELLOW);
        }

        return mSelectedColor;
    }

    public void setSelectedColor(int color) {
        getSharedPreferences().edit().putInt(SELECTED_COLOR, color).commit();
        mSelectedColor = color;
    }

    public boolean isReplaceDelimeters() {
        return getSharedPreferences().getBoolean(REPLACE_DELIMETERS, false);
    }

    public boolean isMultiThreadTasksEnabled(NetworkEnum networkType) {
        if (networkType == NetworkEnum.SFTP || networkType == NetworkEnum.FTP) {
            return false;
        }
        return getSharedPreferences().getBoolean(MULTI_THREAD_TASKS, true);
    }

    public int getMultiActionLabelType() {
        return getSharedPreferences().getInt(MULTI_ACTION_LABEL_TYPE, MULTI_ACTION_LABEL_TYPE_FILES_NUM);
    }

    public int getHiddenColor() {
        if (mHiddenColor == 0) {
            mHiddenColor = getSharedPreferences().getInt(HIDDEN_COLOR, Color.GRAY);
        }

        return mHiddenColor;
    }

    public void setHiddenColor(int color) {
        getSharedPreferences().edit().putInt(HIDDEN_COLOR, color).commit();
        mHiddenColor = color;
    }

    public int getInstallColor() {
        if (mInstallColor == 0) {
            mInstallColor = getSharedPreferences().getInt(INSTALL_COLOR, Color.GREEN);
        }

        return mInstallColor;
    }

    public void setInstallColor(int color) {
        getSharedPreferences().edit().putInt(INSTALL_COLOR, color).commit();
        mInstallColor = color;
    }

    public int getArchiveColor() {
        if (mArchiveColor == 0) {
            mArchiveColor = getSharedPreferences().getInt(ARCHIVE_COLOR, Color.MAGENTA);
        }

        return mArchiveColor;
    }

    public void setArchiveColor(int color) {
        getSharedPreferences().edit().putInt(ARCHIVE_COLOR, color).commit();
        mArchiveColor = color;
    }

    public void resetStyle() {
        getSharedPreferences().edit().remove(MAIN_PANEL_FONT_SIZE)
                .remove(MAIN_PANEL_CELL_MARGIN)
                .remove(BOTTOM_PANEL_FONT_SIZE)
                .remove(VIEWER_FONT_SIZE)
                .remove(MAIN_PANEL_FONT_NAME)
                .remove(VIEWER_FONT_NAME)
                .remove(MAIN_PANEL_COLOR)
                .remove(VIEWER_COLOR)
                .remove(SECONDARY_COLOR)
                .remove(TEXT_COLOR)
                .remove(FOLDER_COLOR)
                .remove(SELECTED_COLOR)
                .remove(HIDDEN_COLOR)
                .remove(INSTALL_COLOR)
                .remove(ARCHIVE_COLOR).commit();

        mMainPanelColor = 0;
        mViewerColor = 0;
        mSecondaryColor = 0;
        mTextColor = 0;
        mFolderColor = 0;
        mHiddenColor = 0;
        mInstallColor = 0;
        mSelectedColor = 0;
        mArchiveColor = 0;

        mMainPanelFontSize = 0;
        mBottomPanelFontSize = 0;
        mViewerFontSize = 0;
        mPanelCellMargin = 0;

        mMainPanelFontType = Typeface.DEFAULT;
        mViewerFontType = Typeface.createFromFile(getViewerFont());
    }

    public boolean isShowSelectedFilesSize() {
        return getSharedPreferences().getBoolean(SHOW_SELECTED_FILES_SIZE, true);
    }

    public boolean isShowQuickActionPanel() {
        return getSharedPreferences().getBoolean(SHOW_QUICK_ACTION_PANEL, true);
    }

    public void exportSettings() {

    }

    public void importSettings() {

    }

    public boolean isHoldAltOnTouch() {
        return getSharedPreferences().getBoolean(HOLD_ALT_BY_CLICK, false);
    }

    public void setSDCardPermissionAsked(boolean value) {
        getSharedPreferences().edit().putBoolean(SDCARD_PERMISSION_ASKED, value).apply();
    }

    public boolean isSDCardPermissionAsked() {
        return getSharedPreferences().getBoolean(SDCARD_PERMISSION_ASKED, false);
    }

    public void setFtpAllowRecursiveDelete(boolean value) {
        getSharedPreferences().edit().putBoolean(FTP_ALLOW_RECURSIVE_DELETE, value).apply();
    }

    public boolean isFtpAllowRecursiveDelete() {
        return getSharedPreferences().getBoolean(FTP_ALLOW_RECURSIVE_DELETE, false);
    }

    public void setDontAskAboutFtpPermission() {
        getSharedPreferences().edit().putBoolean(FTP_ASK_FOR_PERMISSION, false).apply();
    }

    public boolean allowedToAskRecursiveDelete() {
        return getSharedPreferences().getBoolean(FTP_ASK_FOR_PERMISSION, true);
    }
}
