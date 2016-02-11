package com.openfarmanager.android;

import android.app.Application;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import com.openfarmanager.android.controllers.FileSystemController;
import com.openfarmanager.android.core.Settings;
import com.openfarmanager.android.core.ThreadPool;
import com.openfarmanager.android.core.appmanager.AppManager;
import com.openfarmanager.android.core.bookmark.BookmarkManager;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.core.network.dropbox.DropboxAPI;
import com.openfarmanager.android.core.network.ftp.FtpAPI;
import com.openfarmanager.android.core.network.ftp.SftpAPI;
import com.openfarmanager.android.core.network.googledrive.GoogleDriveApi;
import com.openfarmanager.android.core.network.mediafire.MediaFireApi;
import com.openfarmanager.android.core.network.skydrive.SkyDriveAPI;
import com.openfarmanager.android.core.network.smb.SmbAPI;
import com.openfarmanager.android.core.network.yandexdisk.YandexDiskApi;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.utils.SystemUtils;

import java.util.Locale;

public class App extends Application {

    public static App sInstance;

    private AppManager mAppManager;
    private BookmarkManager mBookmarkManager;
    private Settings mSettings;

    private FileSystemController mFileSystemController;

    private ThreadPool mThreadPool;

    protected DropboxAPI mDropboxApi;
    protected SkyDriveAPI mSkyDriveAPI;
    protected FtpAPI mFtpAPI;
    protected SftpAPI mSftpApi;
    protected SmbAPI mSmbAPI;
    protected YandexDiskApi mYandexDiskApi;
    protected GoogleDriveApi mGoogleDriveApi;
    protected MediaFireApi mMediaFireApi;

    @Override
    public void onCreate() {
        super.onCreate();
        SystemUtils.init(this);

        sInstance = this;
        mAppManager = new AppManager();
        mBookmarkManager = new BookmarkManager();
        mSettings = new Settings();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        mDropboxApi = new DropboxAPI(DropboxAPI.createSession());
        mSkyDriveAPI = new SkyDriveAPI();
        mFtpAPI = new FtpAPI();
        mSftpApi = new SftpAPI();
        mSmbAPI = new SmbAPI();
        mYandexDiskApi = new YandexDiskApi();
        mGoogleDriveApi = new GoogleDriveApi();
        mMediaFireApi = new MediaFireApi();

        setLocale();
    }

    @Override
    public void onTerminate() {
        if (mThreadPool != null) {
            mThreadPool.shutdown();
        }
        super.onTerminate();
    }

    public synchronized ThreadPool getThreadPool() {
        if (mThreadPool == null) {
            mThreadPool = new ThreadPool();
        }
        return mThreadPool;
    }

    private void setLocale() {

        boolean forceLocale = mSettings.isForceUseEn();

        if(!forceLocale) {
            return;
        }

        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;

        getBaseContext().getResources().updateConfiguration(config, null);
    }

    public AppManager getAppManager() {
        return mAppManager;
    }

    public BookmarkManager getBookmarkManager() {
        return mBookmarkManager;
    }

    public Settings getSettings() {
        return mSettings;
    }

    public void setFileSystemController(FileSystemController fileSystemController) {
        mFileSystemController = fileSystemController;
    }

    public FileSystemController getFileSystemController() {
        return mFileSystemController;
    }

    public DropboxAPI getDropboxApi() {
        return mDropboxApi;
    }

    public SkyDriveAPI getSkyDriveApi() {
        return mSkyDriveAPI;
    }

    public FtpAPI getFtpApi() {
        return mFtpAPI;
    }

    public SftpAPI getSftpApi() {
        return mSftpApi;
    }

    public SmbAPI getSmbAPI() {
        return mSmbAPI;
    }

    public YandexDiskApi getYandexDiskApi() {
        return mYandexDiskApi;
    }

    public GoogleDriveApi getGoogleDriveApi() {
        return mGoogleDriveApi;
    }

    public MediaFireApi getMediaFireApi() {
        return mMediaFireApi;
    }

    public NetworkApi getNetworkApi(NetworkEnum networkType) {
        switch (networkType) {
            case FTP:
                return mFtpAPI;
            case SFTP:
                return mSftpApi;
            case Dropbox: default:
                return mDropboxApi;
            case SkyDrive:
                return mSkyDriveAPI;
            case SMB:
                return mSmbAPI;
            case YandexDisk:
                return mYandexDiskApi;
            case GoogleDrive:
                return mGoogleDriveApi;
            case MediaFire:
                return mMediaFireApi;
        }
    }
}
