package com.openfarmanager.android.core.network.skydrive;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.annimon.stream.Stream;
import com.microsoft.live.*;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.DataStorageHelper;
import com.openfarmanager.android.core.dbadapters.NetworkAccountDbAdapter;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemScanner;
import com.openfarmanager.android.filesystem.SkyDriveFile;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.openfarmanager.android.utils.Extensions.*;

import java.io.*;
import java.net.URL;
import java.util.*;

import io.reactivex.*;
import io.reactivex.Observable;

/**
 * @author Vlad Namashko
 */
public class SkyDriveAPI implements LiveAuthListener, NetworkApi {

    public static String APP_CLIENT_ID = "00000000440FC95F";
    public static String APP_CLIENT_SECRET = "PGdXoSKea7BFGZDUa4VRRrjVKbhasZtm";

    public static final String SKYDRIVE_ACCESS_TOKEN = "skydrive_access_token";
    public static final String SKYDRIVE_REFRESH_TOKEN = "skydrive_refresh_token";

    protected LiveAuthClient mSkyDriveAuthClient;
    protected LiveConnectClient mSkyDriveConnectClient;

    private Activity mAuthActivity;
    private SkyDriveAccount mCurrentSkyDriveAccount;

//    private final static byte[] BUFFER = new byte[256 * 1024];

    public OnLoginListener mOnLoginListener;

    private String mDriveDefaultPath;

    public static final String[] SCOPES = {
            "wl.signin",
            "wl.basic",
            "wl.offline_access",
            "wl.skydrive_update"
    };

    public SkyDriveAPI() {
        mSkyDriveAuthClient = new LiveAuthClient(App.sInstance, SkyDriveAPI.APP_CLIENT_ID);
    }

    public int getAuthorizedAccountsCount() {
        return NetworkAccountDbAdapter.count(NetworkEnum.SkyDrive.ordinal());
    }

    @Override
    public List<NetworkAccount> getAuthorizedAccounts() {
        List<NetworkAccount> accounts = new ArrayList<NetworkAccount>();
        Cursor cursor = NetworkAccountDbAdapter.getAccounts(NetworkEnum.SkyDrive.ordinal());

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
                    SkyDriveAccount account = new SkyDriveAccount(cursor.getLong(idxId), cursor.getString(idxUserName),
                            data.getString(SKYDRIVE_REFRESH_TOKEN));
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
        return new SkyDriveAccount(-1, App.sInstance.getResources().getString(com.openfarmanager.android.R.string.btn_new), (String) null);
    }

    @Override
    public NetworkAccount getCurrentNetworkAccount() {
        return mCurrentSkyDriveAccount;
    }

    @Override
    public void delete(FileProxy file) throws Exception {
        mSkyDriveConnectClient.delete(file.getFullPath());
    }

    @Override
    public String createDirectory(String baseDirectory, String newDirectoryName) throws Exception {
        JSONObject postData = new JSONObject();
        try {
            postData.put("name", newDirectoryName);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        LiveOperation operation = mSkyDriveConnectClient.post(baseDirectory, postData);

        if (!operation.getResult().has(JsonKeys.ERROR)) {
            return operation.getResult().getString(JsonKeys.ID);
        }

        return null;
    }

    public void deleteCurrentAccount() {
        NetworkAccountDbAdapter.delete(mCurrentSkyDriveAccount.getId());
    }

    public List<FileProxy> getDirectoryFiles(String path) {
        return getDirectoryFiles(path, null);
    }

    public List<FileProxy> getDirectoryFiles(String path, String parentPath) {
        List<FileProxy> files = new ArrayList<>();

        try {
            LiveOperation operation = mSkyDriveConnectClient.get(isNullOrEmpty(path) || path.equals("/") ? "me/skydrive/files" : path + "/files");
            JSONObject result = operation.getResult();

            if (!result.isNull(JsonKeys.DATA)) {
                JSONArray items = (JSONArray) result.get(JsonKeys.DATA);
                for (int i = 0; i < items.length(); i++) {
                    JSONObject data = (JSONObject) items.get(i);
                    files.add(new SkyDriveFile(data, parentPath != null ? parentPath : path));
                }
                FileSystemScanner.sInstance.sort(files);

            }
        } catch (Exception e) {
            throw NetworkException.handleNetworkException(e);
        }

        return files;
    }

    public FileProxy getFileInfo(String id) {

        try {
            LiveOperation operation = mSkyDriveConnectClient.get(id);
            JSONObject result = operation.getResult();
            return new SkyDriveFile(result, "");
        } catch (Exception e) {
            throw NetworkException.handleNetworkException(e);
        }
    }

    @Override
    public io.reactivex.Observable<FileProxy> search(String path, String query) {

        return Observable.create(emitter -> {
            try {
                LiveOperation liveOperation = mSkyDriveConnectClient.get("me/skydrive/search?q=" + query);

                if (liveOperation != null) {
                    JSONObject result = liveOperation.getResult();

                    if (!result.isNull(JsonKeys.DATA)) {
                        JSONArray items = (JSONArray) result.get(JsonKeys.DATA);
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject data = (JSONObject) items.get(i);
//                        searchResult.add(new SkyDriveFile(data, path));
                            emitter.onNext(new SkyDriveFile(data, path));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            emitter.onComplete();
        });
    }

    @Override
    public boolean rename(FileProxy srcFile, String name) throws Exception {
        JSONObject postData = new JSONObject();
        name = name.substring(name.lastIndexOf("/") + 1, name.length());
        try {
            postData.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }


        LiveOperation operation = mSkyDriveConnectClient.put(srcFile.getFullPath(), postData);
        return !operation.getResult().has(JsonKeys.ERROR);
    }

    public void download(FileProxy source, OutputStream outputStream) throws LiveOperationException, IOException, JSONException {
        // get file url
        LiveOperation operation = mSkyDriveConnectClient.get(source.getFullPath());
        // download file (via input stream)
        BufferedInputStream inputStream = new BufferedInputStream(new URL(operation.getResult().getString(JsonKeys.SOURCE)).openStream());
        int len;

        byte[] BUFFER = new byte[256 * 1024];
        while ((len = inputStream.read(BUFFER)) > 0) {
            outputStream.write(BUFFER, 0, len);
        }
        inputStream.close();
        outputStream.close();
    }

    public void setAuthTokensToSession(SkyDriveAccount account, OnLoginListener onLoginListener, String defaultPath) {
        mCurrentSkyDriveAccount = account;

        SharedPreferences settings =
                App.sInstance.getSharedPreferences(PreferencesConstants.FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PreferencesConstants.REFRESH_TOKEN_KEY, account.getToken()).commit();

        mOnLoginListener = onLoginListener;
        mDriveDefaultPath = defaultPath;
        mSkyDriveAuthClient.initialize(this);
    }

    public void startAuthentication(Activity activity, OnLoginListener onLoginListener) {
        mOnLoginListener = onLoginListener;
        mAuthActivity = activity;
        mSkyDriveAuthClient.logout(null);
        mSkyDriveAuthClient.login(mAuthActivity, Arrays.asList(SCOPES), this);
    }

    public LiveConnectClient getConnectClient() {
        return mSkyDriveConnectClient;
    }

    public void onAuthComplete(LiveStatus status, final LiveConnectSession session, Object userState) {
        if (status == LiveStatus.CONNECTED) {
            mAuthActivity = null;
            mSkyDriveConnectClient = new LiveConnectClient(session);

            if (mCurrentSkyDriveAccount == null) {
                mOnLoginListener.onGetUserInfo();
                mSkyDriveConnectClient.getAsync("me", new LiveOperationListener() {
                    @Override
                    public void onError(LiveOperationException exception, LiveOperation operation) {
                        mOnLoginListener.onError(R.string.error_getting_account_info);
                    }

                    @Override
                    public void onComplete(LiveOperation operation) {
                        JSONObject result = operation.getResult();

                        if (result.has(JsonKeys.ERROR)) {
                            JSONObject error = result.optJSONObject(JsonKeys.ERROR);

                            mOnLoginListener.onError(R.string.error_getting_account_info);
                        } else {
                            JSONObject authData = new JSONObject();
                            try {
                                authData.put(SKYDRIVE_ACCESS_TOKEN, session.getAccessToken());
                                authData.put(SKYDRIVE_REFRESH_TOKEN, session.getRefreshToken());
                                NetworkAccountDbAdapter.insert(
                                        result.optString("name") + "(" + result.optString("id") + ")",
                                        NetworkEnum.SkyDrive.ordinal(), authData.toString());

                                mOnLoginListener.onComplete(mDriveDefaultPath);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else {
                mOnLoginListener.onComplete(mDriveDefaultPath);
            }

        } else {
            if (mCurrentSkyDriveAccount != null) {
                mCurrentSkyDriveAccount = null;
                mSkyDriveAuthClient.login(mAuthActivity, Arrays.asList(SCOPES), this);
            } else {
                mOnLoginListener.onError(R.string.error_common);
            }
        }

    }

    public void onAuthError(LiveAuthException exception, Object userState) {
        mSkyDriveConnectClient = null;
        if ("invalid_grant".equals(exception.getError())) {
            mOnLoginListener.onError(R.string.error_account_expired);
        } else {
            mOnLoginListener.onError(R.string.error_common);
        }
    }

    public static class SkyDriveAccount extends NetworkAccount {

        private String mToken;

        public SkyDriveAccount(long id, String userName, JSONObject data) throws JSONException {
            this(id, userName, data.getString(SKYDRIVE_REFRESH_TOKEN));
        }

        public SkyDriveAccount(long id, String userName, String token) {
            mId = id;
            mUserName = userName;
            mToken = token;
        }

        public String getToken() {
            return mToken;
        }

        @Override
        public NetworkEnum getNetworkType() {
            return NetworkEnum.SkyDrive;
        }
    }

    public static interface OnLoginListener {
        public void onGetUserInfo();

        public void onComplete(String defaultPath);

        public void onError(int errorCode);
    }

}
