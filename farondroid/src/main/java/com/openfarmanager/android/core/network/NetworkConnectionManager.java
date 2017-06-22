package com.openfarmanager.android.core.network;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.NetworkAccountChooserAdapter;
import com.openfarmanager.android.adapters.NetworkChooserAdapter;
import com.openfarmanager.android.controllers.FileSystemController;
import com.openfarmanager.android.core.dbadapters.NetworkAccountDbAdapter;
import com.openfarmanager.android.core.network.dropbox.DropboxAPI;
import com.openfarmanager.android.core.network.ftp.FtpAPI;
import com.openfarmanager.android.core.network.ftp.SftpAPI;
import com.openfarmanager.android.core.network.googledrive.GoogleDriveApi;
import com.openfarmanager.android.core.network.mediafire.MediaFireApi;
import com.openfarmanager.android.core.network.skydrive.SkyDriveAPI;
import com.openfarmanager.android.core.network.smb.SmbAPI;
import com.openfarmanager.android.core.network.webdav.WebDavApi;
import com.openfarmanager.android.core.network.yandexdisk.YandexDiskApi;
import com.openfarmanager.android.dialogs.FtpAuthDialog;
import com.openfarmanager.android.dialogs.MediaFireAuthDialog;
import com.openfarmanager.android.dialogs.NetworkScanDialog;
import com.openfarmanager.android.dialogs.SftpAuthDialog;
import com.openfarmanager.android.dialogs.SmbAuthDialog;
import com.openfarmanager.android.dialogs.WebDavAuthDialog;
import com.openfarmanager.android.dialogs.YandexDiskAuthDialog;
import com.openfarmanager.android.fragments.ErrorDialog;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.googledrive.GoogleDriveAuthWindow;
import com.openfarmanager.android.googledrive.model.About;
import com.openfarmanager.android.googledrive.model.Token;
import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.InAppAuthException;
import com.openfarmanager.android.model.exeptions.InitYandexDiskException;
import com.openfarmanager.android.utils.NetworkUtil;
import com.openfarmanager.android.utils.SystemUtils;
import com.openfarmanager.android.view.ToastNotification;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.openfarmanager.android.controllers.FileSystemController.FTP_CONNECTED;
import static com.openfarmanager.android.controllers.FileSystemController.MEDIA_FIRE_CONNECTED;
import static com.openfarmanager.android.controllers.FileSystemController.SFTP_CONNECTED;
import static com.openfarmanager.android.controllers.FileSystemController.SMB_CONNECTED;
import static com.openfarmanager.android.controllers.FileSystemController.SMB_IP_SELECTED;
import static com.openfarmanager.android.controllers.FileSystemController.SMB_SCAN_CANCELED;
import static com.openfarmanager.android.controllers.FileSystemController.SMB_SCAN_NETWORK_REQUESTED;
import static com.openfarmanager.android.controllers.FileSystemController.WEBDAV_CONNECTED;
import static com.openfarmanager.android.controllers.FileSystemController.YANDEX_DISK_CONNECTED;
import static com.openfarmanager.android.utils.Extensions.runAsync;

/**
 * @author Vlad Namashko
 */
public class NetworkConnectionManager {

    private Dialog mProgressDialog;
    private FileSystemController mFileSystemController;
    protected boolean mNetworkAuthRequested;

    protected CompositeDisposable mSubscription;

    public void setRxSubscription(CompositeDisposable subscription) {
        mSubscription = subscription;
    }

    public void setFileSystemController(FileSystemController fileSystemController) {
        mFileSystemController = fileSystemController;
    }

    public boolean isNetworkAuthRequested() {
        return mNetworkAuthRequested;
    }

    public void resetNetworkAuth() {
        mNetworkAuthRequested = false;
    }

    public void openAvailableCloudsList() {
        MainPanel panel = mFileSystemController.getActivePanel();
        if (NetworkUtil.isNetworkAvailable()) {
            showNetworksDialog();
        } else {
            if (panel != null && panel.getActivity() != null) {
                ToastNotification.makeText(panel.getActivity(), App.sInstance.getString(R.string.error_no_network), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void openNetworkBookmark(Bookmark bookmark) {
        openNetworkAccount(bookmark);
    }

    /**
     * Show dialog with available networks (clouds).
     */
    private void showNetworksDialog() {

        final MainPanel panel = mFileSystemController.getActivePanel();
        
        if (panel == null || panel.getActivity() == null) {
            // very weired situation; try to avoid crash.
            return;
        }

        final Dialog dialog = new Dialog(panel.getActivity(), R.style.Action_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = View.inflate(App.sInstance.getApplicationContext(), R.layout.network_type_chooser, null);

        final ListView networks = (ListView) dialogView.findViewById(R.id.network_types);
        final NetworkChooserAdapter adapter = new NetworkChooserAdapter();
        networks.setAdapter(adapter);

        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        networks.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NetworkEnum network = (NetworkEnum) view.getTag();

                switch (network) {
                    case FTP:
                        openFTP(panel);
                        break;
                    case SFTP:
                        openSFTP(panel);
                        break;
                    case SMB:
                        openSmb(panel);
                        break;
                    case Dropbox:
                        openDropbox(panel);
                        break;
                    case SkyDrive:
                        openSkyDrive(panel);
                        break;
                    case YandexDisk:
                        openYandexDisk(panel);
                        break;
                    case GoogleDrive:
                        openGoogleDrive(panel);
                        break;
                    case MediaFire:
                        openMediaFire(panel);
                        break;
                    case WebDav:
                        openWebDav(panel);
                        break;
                }

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.setContentView(dialogView);
        dialog.show();

        adjustDialogSize(dialog, panel);
    }

    public void openSmb(MainPanel panel) {
        SmbAPI api = App.sInstance.getSmbAPI();
        if (api.getAuthorizedAccountsCount() == 0) {
            startSmbAuthentication(panel);
        } else {
            showSelectAccountDialog(NetworkEnum.SMB, panel);
        }
    }

    public void openMediaFire(MainPanel panel) {
        MediaFireApi api = App.sInstance.getMediaFireApi();
        if (api.getAuthorizedAccountsCount() == 0) {
            startMediaFireAuthentication(panel);
        } else {
            showSelectAccountDialog(NetworkEnum.MediaFire, panel);
        }
    }

    public void openFTP(MainPanel panel) {
        FtpAPI api = App.sInstance.getFtpApi();
        if (api.getAuthorizedAccountsCount() == 0) {
            startFtpAuthentication(panel);
        } else {
            showSelectAccountDialog(NetworkEnum.FTP, panel);
        }
    }

    public void openSFTP(MainPanel panel) {
        SftpAPI api = App.sInstance.getSftpApi();
        if (api.getAuthorizedAccountsCount() == 0) {
            startSftpAuthentication(panel);
        } else {
            showSelectAccountDialog(NetworkEnum.SFTP, panel);
        }
    }

    public void openDropbox(MainPanel panel) {
        DropboxAPI api = App.sInstance.getDropboxApi();
        if (api.getAuthorizedAccountsCount() == 0) {
            startDropboxAuthentication(panel);
        } else {
            resetNetworkAuth();
            showSelectAccountDialog(NetworkEnum.Dropbox, panel);
        }
    }

    public void openSkyDrive(MainPanel panel) {
        SkyDriveAPI api = App.sInstance.getSkyDriveApi();
        if (api.getAuthorizedAccountsCount() == 0) {
            // no authorized accounts
            startSkyDriveAuthentication(panel);
        } else {
            showSelectAccountDialog(NetworkEnum.SkyDrive, panel);
        }
    }

    public void openYandexDisk(MainPanel panel) {
        YandexDiskApi api = App.sInstance.getYandexDiskApi();
        if (api.getAuthorizedAccountsCount() == 0) {
            // no authorized accounts
            startYandexDiskAuthentication(panel);
        } else {
            showSelectAccountDialog(NetworkEnum.YandexDisk, panel);
        }
    }

    public void openGoogleDrive(MainPanel panel) {
        GoogleDriveApi api = App.sInstance.getGoogleDriveApi();
        if (api.getAuthorizedAccountsCount() == 0) {
            startGoogleDriveAuthentication(panel);
        } else {
            showSelectAccountDialog(NetworkEnum.GoogleDrive, panel);
        }
    }

    private void openWebDav(MainPanel panel) {
        WebDavApi api = App.sInstance.getWebDavApi();
        if (api.getAuthorizedAccountsCount() == 0) {
            startWebDavAuthentication(panel);
        } else {
            showSelectAccountDialog(NetworkEnum.WebDav, panel);
        }
    }

    private void startFtpAuthentication(MainPanel panel) {
        final Dialog dialog = new FtpAuthDialog(panel.getActivity(), mInAppAuthHandler);
        dialog.show();
        adjustDialogSize(dialog, panel);
    }

    private void startSftpAuthentication(MainPanel panel) {
        final Dialog dialog = new SftpAuthDialog(panel.getActivity(), mInAppAuthHandler);
        dialog.show();
        adjustDialogSize(dialog, panel);
    }

    private void startSmbAuthentication(MainPanel panel) {
        startSmbAuthentication(null, panel);
    }

    private void startSmbAuthentication(String selectedIp, MainPanel panel) {
        final Dialog dialog = new SmbAuthDialog(panel.getActivity(), mInAppAuthHandler, selectedIp);
        dialog.show();
        adjustDialogSize(dialog, panel);
    }

    private void startMediaFireAuthentication(MainPanel panel) {
        final Dialog dialog = new MediaFireAuthDialog(panel.getActivity(), mInAppAuthHandler);
        dialog.show();
        adjustDialogSize(dialog, panel);
    }

    private void startDropboxAuthentication(MainPanel panel) {
        App.sInstance.getDropboxApi().startDropboxAuthentication(panel.getActivity());
        mNetworkAuthRequested = true;
    }

    private void startGoogleDriveAuthentication(MainPanel panel) {
        GoogleDriveAuthWindow popupWindow = new GoogleDriveAuthWindow(panel.getActivity(), mInAppAuthHandler);
        popupWindow.show();
    }

    private void startWebDavAuthentication(MainPanel panel) {
        final Dialog dialog = new WebDavAuthDialog(panel.getActivity(), mInAppAuthHandler);
        dialog.show();
        adjustDialogSize(dialog, panel);
    }

    private void startSkyDriveAuthentication(MainPanel panel) {
        App.sInstance.getSkyDriveApi().startAuthentication(panel.getActivity(), mOnSkyDriveLoginListener);
    }

    private void startYandexDiskAuthentication(MainPanel panel) {
        final Dialog dialog = new YandexDiskAuthDialog(panel.getActivity(), mInAppAuthHandler);
        dialog.show();
        adjustDialogSize(dialog, panel);
    }

    /**
     * Show dialog with available (authenticated) network (cloud) accounts for certain cloud type.
     *
     * @param networkType selected cloud type
     */
    private void showSelectAccountDialog(final NetworkEnum networkType, final MainPanel panel) {

        if (panel == null || panel.getActivity() == null) {
            // very weired situation; try to avoid crash.
            return;
        }

        final Dialog dialog = new Dialog(panel.getActivity(), R.style.Action_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = View.inflate(App.sInstance.getApplicationContext(), R.layout.network_type_chooser, null);
        ((TextView) dialogView.findViewById(R.id.network_dialog_title)).setText(App.sInstance.getString(R.string.action_select_account));

        final ListView networks = (ListView) dialogView.findViewById(R.id.network_types);

        NetworkApi api = App.sInstance.getNetworkApi(networkType);

        final BaseAdapter adapter = new NetworkAccountChooserAdapter(api, new NetworkAccountChooserAdapter.OnDeleteItemListener() {
            @Override
            public void onAccountDelete(NetworkAccount account) {
                NetworkAccountDbAdapter.delete(account.getId());
                ((NetworkAccountChooserAdapter) networks.getAdapter()).dataSetChanged();
            }
        });

        networks.setAdapter(adapter);

        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        networks.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openNetworkAccount((NetworkAccount) view.getTag(), null, panel);

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.setContentView(dialogView);
        dialog.show();

        adjustDialogSize(dialog, panel);
    }

    private void openNetworkAccount(Bookmark bookmark) {
        openNetworkAccount(bookmark.getNetworkAccount(), bookmark.getBookmarkPath(), mFileSystemController.getActivePanel());
    }

    private void openNetworkAccount(NetworkAccount networkAccount, final String path, MainPanel panel) {
        if (networkAccount == null) {
            return;
        }

        switch (networkAccount.getNetworkType()) {
            case FTP:
                final FtpAPI.FtpAccount ftpAccount = (FtpAPI.FtpAccount) networkAccount;
                if (ftpAccount.getServer() == null) { // new
                    startFtpAuthentication(panel);
                } else {
                    showProgressDialog(R.string.connecting_to_ftp);
                    runAsync(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                App.sInstance.getFtpApi().connect(ftpAccount);
                                dismissProgressDialog();
                                openNetworkPanel(NetworkEnum.FTP, path);
                            } catch (final InAppAuthException e) {
                                handleInAppAuthError(e);
                            } catch (final Exception e) {
                                handleNetworkAuthError(e);
                            }
                        }
                    });
                }

                break;
            case SFTP:
                final SftpAPI.SftpAccount sftpAccount = (SftpAPI.SftpAccount) networkAccount;
                if (sftpAccount.getServer() == null) { // new
                    startSftpAuthentication(panel);
                } else {
                    showProgressDialog(R.string.connecting_to_sftp);
                    runAsync(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                App.sInstance.getSftpApi().connect(sftpAccount);
                                dismissProgressDialog();
                                openNetworkPanel(NetworkEnum.SFTP, path);
                            } catch (final InAppAuthException e) {
                                handleInAppAuthError(e);
                            } catch (final Exception e) {
                                handleNetworkAuthError(e);
                            }
                        }
                    });
                }

                break;
            case SMB:
                final SmbAPI.SmbAccount smbAccount = (SmbAPI.SmbAccount) networkAccount;
                if (smbAccount.getDomain() == null) { // new
                    startSmbAuthentication(panel);
                } else {
                    showProgressDialog(R.string.connecting_to_smb);
                    runAsync(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                App.sInstance.getSmbAPI().connect(smbAccount);
                                dismissProgressDialog();
                                openNetworkPanel(NetworkEnum.SMB, path);
                            } catch (final InAppAuthException e) {
                                handleInAppAuthError(e);
                            } catch (final Exception e) {
                                handleNetworkAuthError(e);
                            }
                        }
                    });
                }
                break;
            case Dropbox:
                DropboxAPI.DropboxAccount dropboxAccount = (DropboxAPI.DropboxAccount) networkAccount;
                if (dropboxAccount.getToken() == null) { // new
                    // legacy accounts authorized for previous dropbox api, need to be cleaned
                    if (dropboxAccount.getKey() != null) {
                        ToastNotification.makeText(panel.getActivity(), App.sInstance.getString(R.string.error_account_auth_expired), Toast.LENGTH_LONG).show();
                        NetworkAccountDbAdapter.delete(dropboxAccount.getId());
                        return;
                    }
                    startDropboxAuthentication(panel);
                } else {
                    App.sInstance.getDropboxApi().setAuthTokensToSession(dropboxAccount);
                    openNetworkPanel(NetworkEnum.Dropbox, path);
                }
                break;
            case SkyDrive:
                SkyDriveAPI.SkyDriveAccount skyDriveAccount = (SkyDriveAPI.SkyDriveAccount) networkAccount;
                if (skyDriveAccount.getToken() == null) { // new
                    startSkyDriveAuthentication(panel);
                } else {
                    showProgressDialog(R.string.restoring_skydrive_session);
                    App.sInstance.getSkyDriveApi().setAuthTokensToSession(skyDriveAccount, mOnSkyDriveLoginListener, path);
                }
                break;
            case YandexDisk:
                YandexDiskApi.YandexDiskAccount yandexDiskAccount = (YandexDiskApi.YandexDiskAccount) networkAccount;
                if (yandexDiskAccount.getToken() == null && yandexDiskAccount.getUser() == null) { // new
                    startYandexDiskAuthentication(panel);
                } else {
                    try {
                        App.sInstance.getYandexDiskApi().setupToken(yandexDiskAccount);
                    } catch (InitYandexDiskException e) {
                        e.printStackTrace();
                        showErrorDialog(App.sInstance.getResources().getString(R.string.error_init_yandex_sdk));
                        return;
                    }

                    openNetworkPanel(NetworkEnum.YandexDisk, path);
                }
                break;
            case GoogleDrive:
                final GoogleDriveApi.GoogleDriveAccount driveAccount = (GoogleDriveApi.GoogleDriveAccount) networkAccount;
                if (driveAccount.getToken() == null) { // new
                    startGoogleDriveAuthentication(panel);
                } else {
                    App.sInstance.getGoogleDriveApi().setup(driveAccount);
                    openNetworkPanel(NetworkEnum.GoogleDrive, path);
                }
                break;
            case MediaFire:
                final MediaFireApi.MediaFireAccount account = (MediaFireApi.MediaFireAccount) networkAccount;
                if (account.getPassword() == null) { // new
                    startMediaFireAuthentication(panel);
                } else {
                    showProgressDialog(R.string.loading);
                    Completable.create(source -> {
                        try {
                            App.sInstance.getMediaFireApi().startSession(account);
                            source.onComplete();
                        } catch (Exception e) {
                            source.onError(e);
                        };
                    }).subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            mSubscription.add(d);
                        }

                        @Override
                        public void onComplete() {
                            dismissProgressDialog();
                            openNetworkPanel(NetworkEnum.MediaFire, path);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dismissProgressDialog();
                            ToastNotification.makeText(App.sInstance.getApplicationContext(),
                                    App.sInstance.getString(R.string.mediafire_connection_error), Toast.LENGTH_LONG).show();
                        }
                    });
                }

                break;
            case WebDav:
                final WebDavApi.WebDavAccount webDavAccount = (WebDavApi.WebDavAccount) networkAccount;
                if (webDavAccount.getServer() == null) { // new
                    startWebDavAuthentication(panel);
                } else {
                    showProgressDialog(R.string.connecting_to_webdav);
                    runAsync(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                App.sInstance.getWebDavApi().connect(webDavAccount);
                                dismissProgressDialog();
                                openNetworkPanel(NetworkEnum.WebDav, path);
                            } catch (final InAppAuthException e) {
                                handleInAppAuthError(e);
                            } catch (final Exception e) {
                                handleNetworkAuthError(e);
                            }
                        }
                    });
                }
                break;
        }
    }

    private void openNetworkPanel(NetworkEnum type, String path) {
        mFileSystemController.openNetworkPanel(type, path);
    }

    private void showErrorDialog(String message) {
        try {
            ErrorDialog.newInstance(message).show(mFileSystemController.getActivePanel().getActivity().getSupportFragmentManager(), "errorDialog");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showProgressDialog(final int messageId) {
        try {
            final MainPanel panel = mFileSystemController.getActivePanel();
            panel.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        dismissProgressDialog();
                    }

                    mProgressDialog = new Dialog(panel.getActivity(), android.R.style.Theme_Translucent);
                    mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setContentView(R.layout.dialog_progress);
                    mProgressDialog.show();
                }
            });
            ((TextView) mProgressDialog.findViewById(R.id.progress_bar_text)).setText(messageId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissProgressDialog() {
        mFileSystemController.getActivePanel().getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        });
    }

    private void adjustDialogSize(Dialog dialog, MainPanel panel) {
        adjustDialogSize(dialog, panel, 0.8f);
    }

    /**
     * Adjust dialog size. Actuall for old android version only (due to absence of Holo themes).
     *
     * @param dialog dialog whose size should be adjusted.
     * @param panel active panel
     * @param scaleFactor relative size scale factor (between 0 and 1)
     */
    private void adjustDialogSize(Dialog dialog, MainPanel panel, float scaleFactor) {
        if (!SystemUtils.isHoneycombOrNever()) {
            DisplayMetrics metrics = new DisplayMetrics();
            panel.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(dialog.getWindow().getAttributes());
            params.width = (int) (metrics.widthPixels * scaleFactor);
            params.height = (int) (metrics.heightPixels * scaleFactor);

            dialog.getWindow().setAttributes(params);
        }
    }

    private Handler mInAppAuthHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            MainPanel panel = mFileSystemController.getActivePanel();
            if (msg.what == FTP_CONNECTED) {
                mFileSystemController.openNetworkPanel(NetworkEnum.FTP);
            } else if (msg.what == SFTP_CONNECTED) {
                mFileSystemController.openNetworkPanel(NetworkEnum.SFTP);
            } else if (msg.what == SMB_CONNECTED) {
                mFileSystemController.openNetworkPanel(NetworkEnum.SMB);
            } else if (msg.what == YANDEX_DISK_CONNECTED) {
                mFileSystemController.openNetworkPanel(NetworkEnum.YandexDisk);
            } else if (msg.what == SMB_SCAN_NETWORK_REQUESTED) {
                final Dialog dialog = new NetworkScanDialog(panel.getActivity(), mInAppAuthHandler);
                dialog.show();
                adjustDialogSize(dialog, panel);
            } else if (msg.what == SMB_SCAN_CANCELED) {
                startSmbAuthentication(panel);
            } else if (msg.what == SMB_IP_SELECTED) {
                startSmbAuthentication((String) msg.obj, panel);
            } else if (msg.what == GoogleDriveAuthWindow.MSG_HIDE_LOADING_DIALOG) {
                GoogleDriveApi.GoogleDriveAccount account = null;
                if (msg.arg1 == GoogleDriveAuthWindow.MSG_ARG_SUCCESS) {
                    GoogleDriveApi api = App.sInstance.getGoogleDriveApi();
                    Pair<About, Token> data = (Pair<About, Token>) msg.obj;
                    account = (GoogleDriveApi.GoogleDriveAccount) api.saveAccount(data.first, data.second);
                } else {
                    ToastNotification.makeText(App.sInstance.getApplicationContext(),
                            App.sInstance.getString(R.string.google_drive_get_token_error), Toast.LENGTH_LONG).show();
                }

                dismissProgressDialog();

                if (account != null) {
                    App.sInstance.getGoogleDriveApi().setup(account);
                    mFileSystemController.openNetworkPanel(NetworkEnum.GoogleDrive);
                }
            } else if (msg.what == GoogleDriveAuthWindow.MSG_SHOW_LOADING_DIALOG) {
                showProgressDialog(R.string.loading);
            } else if (msg.what == MEDIA_FIRE_CONNECTED) {
                mFileSystemController.openNetworkPanel(NetworkEnum.MediaFire);
            } else if (msg.what == WEBDAV_CONNECTED) {
                mFileSystemController.openNetworkPanel(NetworkEnum.WebDav);
            }
        }
    };

    private void handleNetworkAuthError(Exception e) {
        mFileSystemController.getActivePanel().getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastNotification.makeText(App.sInstance.getApplicationContext(),
                        App.sInstance.getString(R.string.error_unknown_unexpected_error), Toast.LENGTH_LONG).show();
            }
        });
        dismissProgressDialog();
        e.printStackTrace();
    }

    private void handleInAppAuthError(final InAppAuthException e) {
        mFileSystemController.getActivePanel().getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastNotification.makeText(App.sInstance.getApplicationContext(), e.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });
        dismissProgressDialog();
    }

    private SkyDriveAPI.OnLoginListener mOnSkyDriveLoginListener = new SkyDriveAPI.OnLoginListener() {
        @Override
        public void onGetUserInfo() {
            showProgressDialog(R.string.loading);
        }

        @Override
        public void onComplete(String driveDefaultPath) {
            dismissProgressDialog();
            openNetworkPanel(NetworkEnum.SkyDrive, driveDefaultPath);
        }

        @Override
        public void onError(int errorCode) {
            dismissProgressDialog();
            ToastNotification.makeText(App.sInstance.getApplicationContext(), App.sInstance.getString(errorCode), Toast.LENGTH_LONG).show();
        }
    };

}
