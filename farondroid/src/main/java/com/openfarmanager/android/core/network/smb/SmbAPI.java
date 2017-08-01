package com.openfarmanager.android.core.network.smb;

import android.database.Cursor;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.DataStorageHelper;
import com.openfarmanager.android.core.dbadapters.NetworkAccountDbAdapter;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemScanner;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.InAppAuthException;
import com.openfarmanager.android.model.exeptions.NetworkException;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbSession;

import static com.openfarmanager.android.utils.Extensions.tryParse;

/**
 * @author Vlad Namashko
 */
public class SmbAPI implements NetworkApi {

    public static final String UNKNOWN_USERNAME_OR_BAD_PASSWORD = "Logon failure: unknown user name or bad password.";
    public static final String ACCESS_DENIED = "Access is denied.";

    public static final String SMB_DOMAIN = "domain";
    public static final String SMB_USER = "user";
    public static final String SMB_PASSWORD = "password";

    private UniAddress mDomain;
    private NtlmPasswordAuthentication mAuthentication;

    private SmbAccount mCurrentAccount;

    public void connectAndSave(String address, String username, String password) throws InAppAuthException {
        connect(address, username, password);

        // save account to db
        JSONObject authData = new JSONObject();
        try {
            authData.put(SMB_DOMAIN, address);
            authData.put(SMB_USER, username);
            authData.put(SMB_PASSWORD, password);

            long currentAccountId = NetworkAccountDbAdapter.insert(
                    address + "(" + username + ")",
                    NetworkEnum.SMB.ordinal(), authData.toString());

            List<NetworkAccount> accounts = parseAccounts(NetworkAccountDbAdapter.getAccountById(currentAccountId));

            if (accounts.size() == 1) {
                mCurrentAccount = (SmbAccount) accounts.get(0);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void connect(SmbAccount smbAccount) throws InAppAuthException {
        mCurrentAccount = smbAccount;
        connect(smbAccount.getDomain(), smbAccount.getUser(), smbAccount.getPassword());
    }

    public void connect(String address, String username, String password) throws InAppAuthException {
        try {
            mDomain = UniAddress.getByName(address);
            mAuthentication = new NtlmPasswordAuthentication(address, username, password);
            SmbSession.logon(mDomain, mAuthentication);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new InAppAuthException(App.sInstance.getString(R.string.error_wrong_smb_host));
        } catch (SmbException e) {
            e.printStackTrace();
            throw new InAppAuthException(e.getMessage().equals(UNKNOWN_USERNAME_OR_BAD_PASSWORD) ?
                App.sInstance.getString(R.string.error_smb_wrong_credentials) : App.sInstance.getString(R.string.error_ftp_io));
        }
    }

    public List<FileProxy> getDirectoryFiles(String path) throws NetworkException {
        List<FileProxy> files = new ArrayList<FileProxy>();
        try {
            SmbFile[] listFiles = new SmbFile("smb://" + mDomain.getHostName() + path +
                    (path.endsWith("/") ? "" : "/"), mAuthentication).listFiles();

            for (SmbFile smbFile : listFiles) {
                files.add(new com.openfarmanager.android.filesystem.SmbFile(smbFile, mDomain.getHostName()));
            }
            FileSystemScanner.sInstance.sort(files);
        } catch (SmbException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
    }

    public String getHostName() {
        return mDomain.getHostName();
    }

    public SmbFile createSmbFile(String path) throws MalformedURLException {
        return new SmbFile("smb://" + mDomain.getHostName() + path, mAuthentication);
    }

    public int getAuthorizedAccountsCount() {
        return NetworkAccountDbAdapter.count(NetworkEnum.SMB.ordinal());
    }

    @Override
    public List<NetworkAccount> getAuthorizedAccounts() {
        return parseAccounts(NetworkAccountDbAdapter.getAccounts(NetworkEnum.SMB.ordinal()));
    }

    @Override
    public NetworkAccount newAccount() {
        return new SmbAccount(-1, App.sInstance.getResources().getString(com.openfarmanager.android.R.string.btn_new),
                null, null, null);
    }

    @Override
    public NetworkAccount getCurrentNetworkAccount() {
        return mCurrentAccount;
    }

    @Override
    public void delete(FileProxy file) throws Exception {
        (new SmbFile("smb://" + mDomain.getHostName() + file.getFullPath(), mAuthentication)).delete();
    }

    @Override
    public String createDirectory(String baseDirectory, String newDirectoryName) throws Exception {
        String path  = baseDirectory + "/" + newDirectoryName;
        new SmbFile("smb://" + mDomain.getHostName() + path, mAuthentication).mkdir();
        return path;
    }

    @Override
    public Observable<FileProxy> search(String path, String query) {
        throw new RuntimeException();
    }

    @Override
    public boolean rename(FileProxy srcFile, String s) throws Exception {
        new SmbFile("smb://" + mDomain.getHostName() + srcFile.getFullPath(), mAuthentication).renameTo(new SmbFile(s));
        return true;
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
                    SmbAccount account = new SmbAccount(cursor.getLong(idxId), cursor.getString(idxUserName),
                            data.getString(SMB_DOMAIN), data.getString(SMB_USER), data.getString(SMB_PASSWORD));
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

    public static class SmbAccount extends NetworkAccount {

        private String mDomain;
        private String mUser;
        private String mPassword;

        public SmbAccount(long id, String userName, JSONObject data) throws JSONException {
            this(id, userName, data.getString(SMB_DOMAIN), data.getString(SMB_USER), data.getString(SMB_PASSWORD));
        }

        public SmbAccount(long id, String userName, String domain, String user, String password) {
            mId = id;
            mUserName = userName;
            mDomain = domain;
            mUser = user;
            mPassword = password;
        }

        public String getDomain() {
            return mDomain;
        }

        public String getUser() {
            return mUser;
        }

        public String getPassword() {
            return mPassword;
        }

        @Override
        public NetworkEnum getNetworkType() {
            return NetworkEnum.SMB;
        }
    }
}
