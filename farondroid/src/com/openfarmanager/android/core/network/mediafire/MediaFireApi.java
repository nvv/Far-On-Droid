package com.openfarmanager.android.core.network.mediafire;

import android.database.Cursor;

import com.mediafire.sdk.MFApiException;
import com.mediafire.sdk.MFException;
import com.mediafire.sdk.MediaFire;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.DataStorageHelper;
import com.openfarmanager.android.core.dbadapters.NetworkAccountDbAdapter;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.googledrive.model.Token;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public class MediaFireApi implements NetworkApi {

    public static final String APP_ID = "46558";
    public static final String APP_KEY = "r3s0keye2wi0uucarqnuqerk4cw76h746gh3ernj";

    private MediaFire mMediaFire;
    private MediaFireAccount mCurrentAccount;

    public MediaFireApi() {
        mMediaFire = new MediaFire(MediaFireApi.APP_ID, MediaFireApi.APP_KEY);
    }

    public void startNewSession(String userName, String password) throws MFApiException, MFException {
        mMediaFire.startSessionWithEmail(userName, password, null);
        mCurrentAccount = new MediaFireAccount(saveAccount(userName, password), userName, password);
    }

    public void startSession(MediaFireAccount account) throws MFApiException, MFException {
        mMediaFire.startSessionWithEmail(account.getUserName(), account.getPassword(), null);
        mCurrentAccount = account;
    }

    public void endSession() {
        mMediaFire.endSession();
    }

    @Override
    public int getAuthorizedAccountsCount() {
        return NetworkAccountDbAdapter.count(NetworkEnum.MediaFire.ordinal());
    }

    @Override
    public List<NetworkAccount> getAuthorizedAccounts() {
        List<NetworkAccount> accounts = new ArrayList<>();
        Cursor cursor = NetworkAccountDbAdapter.getAccounts(NetworkEnum.MediaFire.ordinal());

        if (cursor == null) {
            return accounts;
        }

        try {
            int idxId = cursor.getColumnIndex(NetworkAccountDbAdapter.Columns.ID);
            int idxUserName = cursor.getColumnIndex(NetworkAccountDbAdapter.Columns.USER_NAME);
            int idxAuthData = cursor.getColumnIndex(NetworkAccountDbAdapter.Columns.AUTH_DATA);

            while (cursor.moveToNext()) {
                MediaFireAccount account = new MediaFireAccount(cursor.getLong(idxId), cursor.getString(idxUserName),
                        cursor.getString(idxAuthData));
                accounts.add(account);
            }

        } finally {
            cursor.close();
            DataStorageHelper.closeDatabase();
        }

        return accounts;
    }

    @Override
    public NetworkAccount newAccount() {
        return new MediaFireAccount(-1, App.sInstance.getResources().getString(R.string.btn_new), null);
    }

    @Override
    public NetworkAccount getCurrentNetworkAccount() {
        return mCurrentAccount;
    }

    @Override
    public void delete(FileProxy file) throws Exception {

    }

    @Override
    public boolean createDirectory(String path) throws Exception {
        return false;
    }

    @Override
    public List<FileProxy> search(String path, String query) {
        return null;
    }

    @Override
    public boolean rename(String fullPath, String s) throws Exception {
        return false;
    }

    public long saveAccount(String userName, String password) {
        return NetworkAccountDbAdapter.insert(userName, NetworkEnum.MediaFire.ordinal(), password);
    }

    public static class MediaFireAccount extends NetworkAccount {

        private String mPassword;

        public MediaFireAccount(long id, String userName, String password) {
            mId = id;
            mUserName = userName;
            mPassword = password;
        }

        public String getPassword() {
            return mPassword;
        }
    }
}
