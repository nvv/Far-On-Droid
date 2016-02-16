package com.openfarmanager.android.core.network.dropbox;

import android.database.Cursor;

import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.DataStorageHelper;
import com.openfarmanager.android.core.dbadapters.NetworkAccountDbAdapter;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.DropboxFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import org.json.JSONException;
import org.json.JSONObject;

import static com.openfarmanager.android.utils.Extensions.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DropboxAPI extends com.dropbox.client2.DropboxAPI<AndroidAuthSession> implements NetworkApi {

    private final static String APP_KEY = "6gnp59nffh0a5xj";
    private final static String APP_SECRET = "rhm6h3u043l91vv";
    private final static Session.AccessType ACCESS_TYPE = Session.AccessType.DROPBOX;

    public static final String DROPBOX_KEY = "dropbox_key";
    public static final String DROPBOX_SECRET = "dropbox_secret";

    private DropboxAccount mCurrentAuthenticatedAccount;

    public DropboxAPI(AndroidAuthSession session) {
        super(session);
    }

    public static AndroidAuthSession createSession() {
        return new AndroidAuthSession(new AppKeyPair(APP_KEY, APP_SECRET), ACCESS_TYPE);
    }

    public int getAuthorizedAccountsCount() {
        return NetworkAccountDbAdapter.count(NetworkEnum.Dropbox.ordinal());
    }

    public void storeAccessTokens(String userName, AccessTokenPair tokens) {
        JSONObject authData = new JSONObject();
        try {
            authData.put(DROPBOX_KEY, tokens.key);
            authData.put(DROPBOX_SECRET, tokens.secret);
            NetworkAccountDbAdapter.insert(userName, NetworkEnum.Dropbox.ordinal(), authData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<NetworkAccount> getAuthorizedAccounts() {
        List<NetworkAccount> accounts = new ArrayList<NetworkAccount>();
        Cursor cursor = NetworkAccountDbAdapter.getAccounts(NetworkEnum.Dropbox.ordinal());

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
                    DropboxAccount account = new DropboxAccount(cursor.getLong(idxId), cursor.getString(idxUserName),
                            data.getString(DROPBOX_KEY), data.getString(DROPBOX_SECRET));
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

    @Override
    public NetworkAccount newAccount() {
        return new DropboxAccount(-1, App.sInstance.getResources().getString(R.string.btn_new), null, null);
    }

    @Override
    public NetworkAccount getCurrentNetworkAccount() {
        return mCurrentAuthenticatedAccount;
    }

    @Override
    public boolean createDirectory(String path) throws Exception {
        return createFolder(path) != null;
    }

    @Override
    public List<FileProxy> search(String path, String query) {
        List<FileProxy> searchResult = new ArrayList<FileProxy>();
        List<com.dropbox.client2.DropboxAPI.Entry> entries = null;
        try {
            entries = search(isNullOrEmpty(path) ? "/" : path, query, -1, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (entries != null && entries.size() > 0) {
            for (Entry entry : entries) {
                searchResult.add(new DropboxFile(entry));
            }
        }

        return searchResult;
    }

    @Override
    public boolean rename(FileProxy file, String toPath) throws Exception {
        return move(file.getFullPath(), toPath) != null;
    }

    public void setAuthTokensToSession(DropboxAccount account) {
        mCurrentAuthenticatedAccount = account;
        getSession().setAccessTokenPair(new AccessTokenPair(account.getKey(), account.getSecret()));
    }

    public void deleteCurrentAccount() {
        NetworkAccountDbAdapter.delete(mCurrentAuthenticatedAccount.getId());
    }

    @Override
    public void delete(FileProxy file) throws Exception {
        try {
            delete(file.getFullPath());
        } catch (DropboxServerException e) {
            if (e.error == DropboxServerException._503_SERVICE_UNAVAILABLE) {
                delete(file);
            } else {
                throw e;
            }
        }
    }

    @Override
    public DropboxAPI.Entry putFileOverwrite(String path, InputStream is, long length, ProgressListener listener) throws DropboxException {
        try {
            return super.putFileOverwrite(path, is, length, listener);
        } catch (DropboxServerException e) {
            if (e.error == DropboxServerException._503_SERVICE_UNAVAILABLE) {
                return putFileOverwrite(path, is, length, listener);
            } else {
                throw e;
            }
        }
    }

    public static class DropboxAccount extends NetworkAccount {

        private String mKey;
        private String mSecret;

        public DropboxAccount(long id, String userName, JSONObject data) throws JSONException {
            this(id, userName, data.getString(DROPBOX_KEY), data.getString(DROPBOX_SECRET));
        }

        public DropboxAccount(long id, String userName, String key, String secret) {
            mId = id;
            mUserName = userName;
            mKey = key;
            mSecret = secret;
        }

        public String getKey() {
            return mKey;
        }

        public String getSecret() {
            return mSecret;
        }

        @Override
        public NetworkEnum getNetworkType() {
            return NetworkEnum.Dropbox;
        }
    }
}
