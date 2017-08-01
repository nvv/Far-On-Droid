package com.openfarmanager.android.core.network.dropbox;

import android.app.Activity;
import android.database.Cursor;

import com.annimon.stream.Stream;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.RateLimitException;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeletedMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.SearchResult;
import com.dropbox.core.v2.files.UploadUploader;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.users.FullAccount;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.DataStorageHelper;
import com.openfarmanager.android.core.dbadapters.NetworkAccountDbAdapter;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.DropboxFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.utils.Extensions;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class DropboxAPI implements NetworkApi {

    private final static String APP_KEY = "6gnp59nffh0a5xj";
    private final static String APP_SECRET = "rhm6h3u043l91vv";

    public static final String DROPBOX_KEY = "dropbox_key";
    public static final String DROPBOX_SECRET = "dropbox_secret";
    public static final String DROPBOX_TOKEN = "dropbox_token";

    private DbxClientV2 mDropboxClient;

    private DropboxAccount mCurrentAuthenticatedAccount;

    public DropboxAPI() {
    }

    public void startDropboxAuthentication(Activity activity) {
        Auth.startOAuth2Authentication(activity, APP_KEY);
    }

    public String getOauthToken() {
        return Auth.getOAuth2Token();
    }

    public void initSession(String token) {
        mDropboxClient = new DbxClientV2(DbxRequestConfig.newBuilder("far_dropbox").build(), token);
    }

    public FullAccount getAccountInfo() throws DbxException {
        return mDropboxClient.users().getCurrentAccount();
    }

    public String getAccountDisplayName() throws DbxException {
        FullAccount account = getAccountInfo();
        String userName = account.getName().getDisplayName();
        if (!Extensions.isNullOrEmpty(account.getEmail())) {
            userName += "(" + account.getEmail() + ")";
        }

        return userName;
    }

    public List<Metadata> listFiles(String path) throws DbxException {
        return mDropboxClient.files().listFolder("/".equals(path) ? "" : path).getEntries();
    }

    public int getAuthorizedAccountsCount() {
        return NetworkAccountDbAdapter.count(NetworkEnum.Dropbox.ordinal());
    }

    public void storeAccessTokens(String userName, String token) {
        JSONObject authData = new JSONObject();
        try {
            authData.put(DROPBOX_TOKEN, token);
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
                            data.has(DROPBOX_KEY) ? data.getString(DROPBOX_KEY) : null,
                            data.has(DROPBOX_SECRET) ? data.getString(DROPBOX_SECRET) : null,
                            data.has(DROPBOX_TOKEN) ? data.getString(DROPBOX_TOKEN) : null);
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
        return new DropboxAccount(-1, App.sInstance.getResources().getString(R.string.btn_new), (String) null);
    }

    @Override
    public NetworkAccount getCurrentNetworkAccount() {
        return mCurrentAuthenticatedAccount;
    }

    @Override
    public String createDirectory(String baseDirectory, String newDirectoryName) throws Exception {
        return mDropboxClient.files().createFolder(baseDirectory + "/" + newDirectoryName).getPathLower();
    }

    @Override
    public Observable<FileProxy> search(String path, String query) {
        return Observable.create(emitter -> {
            try {
                SearchResult result = mDropboxClient.files().search("/".equals(path) ? "" : path, query);
                if (result != null && result.getMatches() != null) {
                    Stream.of(result.getMatches()).
                            filter(match -> match != null && !(match.getMetadata() instanceof DeletedMetadata)).
                            forEach(match -> emitter.onNext(new DropboxFile(match.getMetadata())));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            emitter.onComplete();
        });
    }

    public String getFileLink(FileProxy proxy) throws DbxException {
        return mDropboxClient.files().getTemporaryLink(proxy.getFullPath()).getLink();
    }

    @Override
    public boolean rename(FileProxy file, String toPath) throws Exception {
        return mDropboxClient.files().move(file.getFullPath(), toPath) != null;
    }

    public void setAuthTokensToSession(DropboxAccount account) {
        mCurrentAuthenticatedAccount = account;
        initSession(account.getToken());
    }

    public void deleteCurrentAccount() {
        NetworkAccountDbAdapter.delete(mCurrentAuthenticatedAccount.getId());
    }

    @Override
    public void delete(FileProxy file) throws Exception {
        try {
            mDropboxClient.files().delete(file.getFullPath());
        } catch (RateLimitException e) {
            if (e.getMessage().contains("too_many_write_operations")) {
                Thread.sleep(250);
                delete(file);
            } else {
                throw e;
            }
        }
    }

    public UploadUploader uploadFile(String path) throws DbxException {
        return mDropboxClient.files().uploadBuilder(path).withMode(WriteMode.OVERWRITE).start();
    }

    public void downloadFile(String path, OutputStream outputStream) throws IOException, DbxException {
        mDropboxClient.files().download(path).download(outputStream);
    }

    public String share(String fullPath) throws Exception {
        return mDropboxClient.sharing().createSharedLinkWithSettings(fullPath).getUrl();
    }

    public static class DropboxAccount extends NetworkAccount {

        @Deprecated
        private String mKey;
        @Deprecated
        private String mSecret;

        private String mToken;

        public DropboxAccount(long id, String userName, JSONObject data) throws JSONException {
            this(id, userName, data.getString(DROPBOX_TOKEN));
        }

        public DropboxAccount(long id, String userName, String key, String secret, String token) {
            mId = id;
            mUserName = userName;
            mKey = key;
            mSecret = secret;
            mToken = token;
        }

        public DropboxAccount(long id, String userName, String token) {
            mId = id;
            mUserName = userName;
            mToken = token;
        }

        @Deprecated
        public String getKey() {
            return mKey;
        }

        @Deprecated
        public String getSecret() {
            return mSecret;
        }

        public String getToken() {
            return mToken;
        }

        @Override
        public NetworkEnum getNetworkType() {
            return NetworkEnum.Dropbox;
        }

    }
}
