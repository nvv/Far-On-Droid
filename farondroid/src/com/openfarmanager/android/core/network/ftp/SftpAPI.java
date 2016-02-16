package com.openfarmanager.android.core.network.ftp;

import android.database.Cursor;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.DataStorageHelper;
import com.openfarmanager.android.core.dbadapters.NetworkAccountDbAdapter;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemScanner;
import com.openfarmanager.android.filesystem.SftpFile;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.InAppAuthException;
import com.openfarmanager.android.model.exeptions.NetworkException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import static com.openfarmanager.android.utils.Extensions.tryParse;

/**
 * @author Vlad Namashko
 */
public class SftpAPI implements NetworkApi {

    public static final String SFTP_SERVER = "server";
    public static final String SFTP_PORT = "port";
    public static final String SFTP_USER = "user";
    public static final String SFTP_PASSWORD = "password";
    public static final String SFTP_LOGIN_BY_PRIVATE_KEY = "login_by_private_key";
    public static final String SFTP_PRIVATE_KEY = "private_key";

    private ChannelSftp mSftpChannel;

    private SftpAccount mCurrentAccount;

    public void connectAndSave(String server, int port, String user, String password,
                               boolean isLoginByPrivateKey, byte[] privateKey) throws InAppAuthException {
        connect(server, port, user, password, isLoginByPrivateKey, privateKey);

        // save account to db
        JSONObject authData = new JSONObject();
        try {
            authData.put(SFTP_SERVER, server);
            authData.put(SFTP_PORT, port);
            authData.put(SFTP_USER, user);
            authData.put(SFTP_PASSWORD, password);
            authData.put(SFTP_LOGIN_BY_PRIVATE_KEY, isLoginByPrivateKey);
            //authData.put(SFTP_PRIVATE_KEY, privateKey);

            long currentAccountId = NetworkAccountDbAdapter.insert(
                    server + "(" + user + ")",
                    NetworkEnum.SFTP.ordinal(), authData.toString());

            List<NetworkAccount> accounts = parseAccounts(NetworkAccountDbAdapter.getAccountById(currentAccountId));

            if (accounts.size() == 1) {
                mCurrentAccount = (SftpAccount) accounts.get(0);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void connect(SftpAccount sftpAccount) throws InAppAuthException {
        mCurrentAccount = sftpAccount;
        connect(sftpAccount.getServer(), sftpAccount.getPort(), sftpAccount.getUser(),
                sftpAccount.getPassword(), sftpAccount.isLoginByPrivateKey(), sftpAccount.getPrivateKey());
    }

    public void connect(String server, int port, String user, String password,
                        boolean isLoginByPrivateKey, byte[] privateKey) throws InAppAuthException {
        JSch jsch = new JSch();
        Session session;
        try {
            session = jsch.getSession(user, server, port);
        } catch (JSchException e) {
            e.printStackTrace();
            throw new InAppAuthException(App.sInstance.getString(R.string.error_sftp_connection_error));
        }
        session.setPassword(password);
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);
        Channel channel;
        try {
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            throw new InAppAuthException(App.sInstance.getString(R.string.error_sftp_connection_error));
        }
        mSftpChannel = (ChannelSftp) channel;

        String charset = App.sInstance.getSettings().getSftpCharset(server);
        if (charset != null) {
            setCharsetEncoding(charset);
        }
    }

    private void setCharsetEncoding(String charset) {
        try {
            mSftpChannel.setFilenameEncoding(charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    SftpAccount account = new SftpAccount(cursor.getLong(idxId), cursor.getString(idxUserName),
                            data.getString(SFTP_SERVER), tryParse(data.getString(SFTP_PORT), 22),
                            data.getString(SFTP_USER), data.getString(SFTP_PASSWORD),
                            data.getBoolean(SFTP_LOGIN_BY_PRIVATE_KEY), null);
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

    public List<FileProxy> getDirectoryFiles(String path) throws NetworkException {
        List<FileProxy> files = new ArrayList<FileProxy>();
        try {
            if (!path.equals(mSftpChannel.pwd())) {
                mSftpChannel.cd(path);
            }
            Vector sftpFiles = mSftpChannel.ls(path);
            for (Object entry : sftpFiles) {
                ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) entry;
                if (!lsEntry.getFilename().equals(".") && !lsEntry.getFilename().equals("..")) {
                    files.add(new SftpFile(path, lsEntry));
                }
            }
            FileSystemScanner.sInstance.sort(files);
        } catch (SftpException e) {
            throw NetworkException.handleNetworkException(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw NetworkException.handleNetworkException(e);
        }

        return files;
    }

    @Override
    public int getAuthorizedAccountsCount() {
        return NetworkAccountDbAdapter.count(NetworkEnum.SFTP.ordinal());
    }

    @Override
    public List<NetworkAccount> getAuthorizedAccounts() {
        return parseAccounts(NetworkAccountDbAdapter.getAccounts(NetworkEnum.SFTP.ordinal()));
    }

    @Override
    public NetworkAccount newAccount() {
        return new SftpAccount(-1, App.sInstance.getResources().getString(com.openfarmanager.android.R.string.btn_new),
                null, 0, null, null, false, null);
    }

    @Override
    public NetworkAccount getCurrentNetworkAccount() {
        return mCurrentAccount;
    }

    @Override
    public void delete(FileProxy file) throws Exception {
        changeDirectory(file.getParentPath());
        if (file.isDirectory()) {
            mSftpChannel.rmdir(file.getFullPath());
        } else {
            mSftpChannel.rm(file.getFullPath());
        }
    }

    public void setCharset(Charset charset) {
        setCharsetEncoding(charset.name());
        App.sInstance.getSettings().saveCharset(mCurrentAccount.getServer(), charset.name());
    }

    public OutputStream getUploadStream(String path) throws SftpException {
        return mSftpChannel.put(path);
    }

    public void writeFileToStream(String filePath, OutputStream dst) throws SftpException {
        mSftpChannel.get(filePath, dst);
    }

    @Override
    public boolean createDirectory(String path) throws Exception {
        mSftpChannel.mkdir(path);
        return true;
    }

    @Override
    public List<FileProxy> search(String path, String query) {
        throw new RuntimeException();
    }

    @Override
    public boolean rename(FileProxy srcFile, String s) throws Exception {
        String fullPath = srcFile.getFullPath();
        mSftpChannel.rename(fullPath, s.substring(s.lastIndexOf('/') + 1));
        return true;
    }

    public void closeChannel() {
        if (mSftpChannel != null && mSftpChannel.isConnected()) {
            mSftpChannel.exit();
        }
    }

    public void changeDirectory(String destination) throws SftpException {
        mSftpChannel.cd(destination);
    }

    public static class SftpAccount extends NetworkAccount {

        private String mServer;
        private int mPort;
        private String mUser;
        private String mPassword;
        private boolean mIsLoginByPrivateKey;
        private byte[] mPrivateKey;

        public SftpAccount(long id, String userName, JSONObject data) throws JSONException {
            this(id, userName, data.getString(SFTP_SERVER), tryParse(data.getString(SFTP_PORT), 22),
                    data.getString(SFTP_USER), data.getString(SFTP_PASSWORD),
                    data.getBoolean(SFTP_LOGIN_BY_PRIVATE_KEY), null);
        }

        public SftpAccount(long id, String userName, String server, int port, String user,
                           String password, boolean isLoginByPrivateKey, byte[] privateKey ) {
            mId = id;
            mUserName = userName;
            mServer = server;
            mPort = port;
            mUser = user;
            mPassword = password;
            mIsLoginByPrivateKey = isLoginByPrivateKey;
            mPrivateKey = privateKey;
        }

        public String getServer() {
            return mServer;
        }

        public int getPort() {
            return mPort;
        }

        public String getUser() {
            return mUser;
        }

        public String getPassword() {
            return mPassword;
        }

        public boolean isLoginByPrivateKey() {
            return mIsLoginByPrivateKey;
        }

        public byte[] getPrivateKey() {
            return mPrivateKey;
        }

        @Override
        public NetworkEnum getNetworkType() {
            return NetworkEnum.SFTP;
        }
    }
}
