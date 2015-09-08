package com.openfarmanager.android.core.network.yandexdisk;

import android.database.Cursor;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.DataStorageHelper;
import com.openfarmanager.android.core.dbadapters.NetworkAccountDbAdapter;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.YandexDiskFile;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.InitYandexDiskException;
import com.openfarmanager.android.model.exeptions.NetworkException;
import com.yandex.disk.client.Credentials;
import com.yandex.disk.client.ListItem;
import com.yandex.disk.client.ListParsingHandler;
import com.yandex.disk.client.TransportClient;
import com.yandex.disk.client.exceptions.WebdavClientInitException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public class YandexDiskApi implements NetworkApi {

    public static final String CLIENT_ID = "f16fa6cd94954c2ca68ccb7ca626f29b";
    public static final String CLIENT_SECRET = " c2b0f694984248178f3ab50cc020889e";

    private static String YANDEX_TOKEN = "yandex_disk_token";

    public static final String ACCOUNT_TYPE = "com.yandex";
    public static final String AUTH_URL = "https://oauth.yandex.ru/authorize?response_type=token&client_id=" + CLIENT_ID;
    private static final String ACTION_ADD_ACCOUNT = "com.yandex.intent.ADD_ACCOUNT";
    private static final String KEY_CLIENT_SECRET = "clientSecret";

    private NetworkAccount mCurrentAccount;
    private TransportClient mTransportClient;

    public NetworkAccount saveAccount(Credentials credentials) {
        JSONObject authData = new JSONObject();
        try {
            authData.put(YANDEX_TOKEN, credentials.getToken());
            long id = NetworkAccountDbAdapter.insert(credentials.getUser(), NetworkEnum.YandexDisk.ordinal(), authData.toString());

            Cursor cursor = NetworkAccountDbAdapter.getAccountById(id);
            if (cursor != null) {
                int idxId = cursor.getColumnIndex(NetworkAccountDbAdapter.Columns.ID);
                int idxUserName = cursor.getColumnIndex(NetworkAccountDbAdapter.Columns.USER_NAME);
                int idxAuthData = cursor.getColumnIndex(NetworkAccountDbAdapter.Columns.AUTH_DATA);

                cursor.moveToNext();
                String authDataString = cursor.getString(idxAuthData);
                JSONObject data = new JSONObject(authDataString);
                return new YandexDiskAccount(cursor.getLong(idxId), cursor.getString(idxUserName), data.getString(YANDEX_TOKEN));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setupToken(NetworkAccount networkAccount) throws InitYandexDiskException{
        mCurrentAccount = networkAccount;
        try {
            mTransportClient = TransportClient.getInstance(App.sInstance.getApplicationContext(),
                    new Credentials(networkAccount.getUserName(), ((YandexDiskAccount) networkAccount).getToken()));
        } catch (WebdavClientInitException e) {
            e.printStackTrace();
            throw new InitYandexDiskException();
        }
    }

    public List<FileProxy> getDirectoryFiles(String path) throws NetworkException {
        List<FileProxy> files = new ArrayList<FileProxy>();
        final List<ListItem> fileItemList = new ArrayList<ListItem>();
        try {
            mTransportClient.getList(path, new ListParsingHandler() {

                // First item in PROPFIND is the current collection name
                boolean ignoreFirstItem = true;

                @Override
                public boolean hasCancelled() {
                    return false;
                }

                @Override
                public void onPageFinished(int itemsOnPage) {

                }

                @Override
                public boolean handleItem(ListItem item) {
                    if (ignoreFirstItem) {
                        ignoreFirstItem = false;
                        return false;
                    } else {
                        fileItemList.add(item);
                        return true;
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            throw NetworkException.handleNetworkException(ex);
        }

        for (ListItem item : fileItemList) {
            files.add(new YandexDiskFile(item));
        }

        return files;
    }

    public TransportClient client() {
        return mTransportClient;
    }

    @Override
    public int getAuthorizedAccountsCount() {
        return NetworkAccountDbAdapter.count(NetworkEnum.YandexDisk.ordinal());
    }

    @Override
    public List<NetworkAccount> getAuthorizedAccounts() {
        List<NetworkAccount> accounts = new ArrayList<NetworkAccount>();
        Cursor cursor = NetworkAccountDbAdapter.getAccounts(NetworkEnum.YandexDisk.ordinal());

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
                    YandexDiskAccount account = new YandexDiskAccount(cursor.getLong(idxId), cursor.getString(idxUserName),
                            data.getString(YANDEX_TOKEN));
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
        return new YandexDiskAccount(-1, App.sInstance.getResources().getString(R.string.btn_new), null);
    }

    @Override
    public NetworkAccount getCurrentNetworkAccount() {
        return mCurrentAccount;
    }

    @Override
    public void delete(FileProxy file) throws Exception {
        mTransportClient.delete(file.getFullPath());
    }

    @Override
    public boolean createDirectory(String path) throws Exception {
        mTransportClient.makeFolder(path);
        return true;
    }

    @Override
    public List<FileProxy> search(String path, String query) {
        return null;
    }

    @Override
    public boolean rename(FileProxy srcFile, String s) throws Exception {
        mTransportClient.move(srcFile.getFullPath(), s);
        return true;
    }

    public static class YandexDiskAccount extends NetworkAccount {

        private String mToken;

        public YandexDiskAccount(long id, String userName, String token) {
            mId = id;
            mUserName = userName;
            mToken = token;
        }

        public String getToken() {
            return mToken;
        }
    }
}
