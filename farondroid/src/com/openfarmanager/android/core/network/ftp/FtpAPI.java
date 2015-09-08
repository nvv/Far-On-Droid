package com.openfarmanager.android.core.network.ftp;

import android.database.Cursor;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.DataStorageHelper;
import com.openfarmanager.android.core.dbadapters.NetworkAccountDbAdapter;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemScanner;
import com.openfarmanager.android.filesystem.FtpFile;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.InAppAuthException;
import com.openfarmanager.android.model.exeptions.NetworkException;

import static com.openfarmanager.android.utils.Extensions.tryParse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;

/**
 * @author Vlad Namashko
 */
public class FtpAPI implements NetworkApi {

    public static final String FTP_SERVER = "server";
    public static final String FTP_PORT = "port";
    public static final String FTP_MODE = "mode";
    public static final String FTP_USER = "user";
    public static final String FTP_PASSWORD = "password";

    private FTPClient mFtpClient;

    private FtpAccount mCurrentAccount;

    public void connectAndSave(String server, int port, boolean activeMode,
                        String user, String password) throws InAppAuthException {
        connect(server, port, activeMode, user, password);

        // save account to db
        JSONObject authData = new JSONObject();
        try {
            authData.put(FTP_SERVER, server);
            authData.put(FTP_PORT, port);
            authData.put(FTP_MODE, activeMode);
            authData.put(FTP_USER, user);
            authData.put(FTP_PASSWORD, password);

            long currentAccountId = NetworkAccountDbAdapter.insert(
                    server + "(" + user + ")",
                    NetworkEnum.FTP.ordinal(), authData.toString());

            List<NetworkAccount> accounts = parseAccounts(NetworkAccountDbAdapter.getAccountById(currentAccountId));

            if (accounts.size() == 1) {
                mCurrentAccount = (FtpAccount) accounts.get(0);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void connect(FtpAccount ftpAccount) throws InAppAuthException {
        mCurrentAccount = ftpAccount;
        connect(ftpAccount.getServer(), ftpAccount.getPort(), ftpAccount.getMode(),
                ftpAccount.getUser(), ftpAccount.getPassword());
    }

    public void connect(String server, int port, boolean activeMode,
                           String user, String password) throws InAppAuthException {
        mFtpClient = new FTPClient();
        try {
            mFtpClient.connect(server, port);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InAppAuthException(App.sInstance.getString(R.string.error_wrong_ftp_host));
        }

        try {
            mFtpClient.login(user, password);
        } catch (FTPException e) {
            throw new InAppAuthException(App.sInstance.getString(R.string.error_wrong_credentials));
        } catch (Exception e) {
            e.printStackTrace();
            throw new InAppAuthException(App.sInstance.getString(R.string.error_ftp_io));
        }

        String charset = App.sInstance.getSettings().getCharset(server);
        if (charset != null) {
            mFtpClient.setCharset(charset);
        }

        //mFtpClient.setType(FTPClient.TYPE_BINARY);
        mFtpClient.setPassive(!activeMode);
    }

    public List<FileProxy> getDirectoryFiles(String path) throws NetworkException {
        List<FileProxy> files = new ArrayList<FileProxy>();
        try {
            if (!path.equals(mFtpClient.currentDirectory())) {
                mFtpClient.changeDirectory(path);
            }
            FTPFile[] ftpFiles = mFtpClient.list();
            for (FTPFile ftpFile : ftpFiles) {
                files.add(new FtpFile(path, ftpFile));
            }
            FileSystemScanner.sInstance.sort(files);
        } catch (SocketTimeoutException e) {
            throw NetworkException.handleNetworkException(e);
        } catch (IOException e) {
            if ("FTPConnection closed".equals(e.getMessage())) {
                throw NetworkException.handleNetworkException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //throw NetworkException.handleNetworkException(e);
        }

        return files;
    }

    public void setCharset(Charset charset) {
        mFtpClient.setCharset(charset.name());
        App.sInstance.getSettings().saveCharset(mFtpClient.getHost(), charset.name());
    }

    public int getAuthorizedAccountsCount() {
        return NetworkAccountDbAdapter.count(NetworkEnum.FTP.ordinal());
    }

    @Override
    public List<NetworkAccount> getAuthorizedAccounts() {
        return parseAccounts(NetworkAccountDbAdapter.getAccounts(NetworkEnum.FTP.ordinal()));
    }

    private List<NetworkAccount> parseAccounts(Cursor cursor) {
        List<NetworkAccount> accounts = new ArrayList<NetworkAccount>();
        if (cursor == null) {
            return accounts;
        }

        try {
            int idxId = cursor.getColumnIndex(NetworkAccountDbAdapter.Columns.ID);
            int idxUserName = cursor.getColumnIndex(NetworkAccountDbAdapter.Columns.USER_NAME);
            int idxAuthData = cursor.getColumnIndex(NetworkAccountDbAdapter.Columns.AUTH_DATA);

            while(cursor.moveToNext()) {
                String authData = cursor.getString(idxAuthData);
                try {
                    JSONObject data = new JSONObject(authData);
                    FtpAccount account = new FtpAccount(cursor.getLong(idxId), cursor.getString(idxUserName),
                            data.getString(FTP_SERVER), tryParse(data.getString(FTP_PORT), 21),
                            tryParse(data.getString(FTP_MODE), false), data.getString(FTP_USER), data.getString(FTP_PASSWORD));
                    accounts.add(account);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } finally {
            cursor.close();
            DataStorageHelper.closeDatabase();
        }

        return accounts;
    }

    public FTPClient client() {
        return mFtpClient;
    }

    @Override
    public NetworkAccount newAccount() {
        return new FtpAccount(-1, App.sInstance.getResources().getString(com.openfarmanager.android.R.string.btn_new),
                null, 0, false, null, null);
    }

    @Override
    public NetworkAccount getCurrentNetworkAccount() {
        return mCurrentAccount;
    }

    @Override
    public void delete(FileProxy file) throws Exception {
        if (file.isDirectory()) {
            mFtpClient.deleteDirectory(file.getFullPath());
        } else {
            mFtpClient.deleteFile(file.getFullPath());
        }
    }

    @Override
    public boolean createDirectory(String path) throws Exception {
        mFtpClient.createDirectory(path);
        return true;
    }

    @Override
    public List<FileProxy> search(String path, String query) {
        throw new RuntimeException();
    }

    @Override
    public boolean rename(FileProxy srcFile, String s) throws Exception {
        String fullPath = srcFile.getFullPath();
        String workingDir = fullPath.substring(0, fullPath.lastIndexOf('/') + 1);
        mFtpClient.changeDirectory(workingDir);
        mFtpClient.rename(fullPath.substring(fullPath.lastIndexOf('/') + 1), s.substring(s.lastIndexOf('/') + 1));
        return true;
    }

    public static class FtpAccount extends NetworkAccount {

        private String mServer;
        private int mPort;
        private boolean mMode;
        private String mUser;
        private String mPassword;

        public FtpAccount(long id, String userName, String server, int port, boolean mode, String user, String password) {
            mId = id;
            mUserName = userName;
            mServer = server;
            mPort = port;
            mMode = mode;
            mUser = user;
            mPassword = password;
        }

        public String getServer() {
            return mServer;
        }

        public int getPort() {
            return mPort;
        }

        public boolean getMode() {
            return mMode;
        }

        public String getUser() {
            return mUser;
        }

        public String getPassword() {
            return mPassword;
        }

    }

}
