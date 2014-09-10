package com.openfarmanager.android.core.network.bitcasa;

import android.database.Cursor;
import android.os.Handler;
import android.os.Message;

import com.bitcasa.client.BitcasaClient;
import com.bitcasa.client.HTTP.BitcasaRESTConstants;
import com.bitcasa.client.datamodel.AccountInfo;
import com.bitcasa.client.datamodel.FileMetaData;
import com.bitcasa.client.exception.BitcasaException;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.DataStorageHelper;
import com.openfarmanager.android.core.dbadapters.NetworkAccountDbAdapter;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.BitcasaFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.googledrive.model.About;
import com.openfarmanager.android.googledrive.model.Token;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.utils.Extensions;
import com.openfarmanager.android.view.BitcasaLoginDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;

/**
 * author: Vlad Namashko
 */
public class BitcasaApi implements NetworkApi {

    private final static String CLIENT_ID = "280e9b83";
    private final static String CLIENT_SECRET = "a997bc4dc1bc41decb22b43950b16678";

    private static String ACCESS_TOKEN = "access_token";

    private BitcasaClient mBitcasaClient;

    private BitcasaAccount mCurrentAccount;

    private HashMap<String, String> mFoldersAliases = new HashMap<String, String>();

    public BitcasaApi() {
        mBitcasaClient = new BitcasaClient(CLIENT_ID, CLIENT_SECRET);
    }

    public void setup(String accessToken) {
        mBitcasaClient.setAccessToken(accessToken);
    }

    public HashMap<String, String> getFoldersAliases() {
        return mFoldersAliases;
    }

    public String findInPathAliases(String path) {
        for (Map.Entry<String, String> fileAlias : mFoldersAliases.entrySet()) {
            if (fileAlias.getValue().equals(path)) {
                return fileAlias.getKey();
            }
        }

        return null;
    }

    public String findPathId(String path) {
        if (isNullOrEmpty(path)) {
            path = "/";
        }

        if (path.endsWith("/") && !path.equals("/")) {
            path = path.substring(0, path.length() - 1);
        }

        String findResult = findInPathAliases(path);

        return findResult == null ? path : findResult;
    }

    public void setCurrentAccount(BitcasaAccount account) {
        mCurrentAccount = account;
    }

    public String getAccessToken(String authCode) throws IOException, BitcasaException {
        return mBitcasaClient.getAccessToken(CLIENT_SECRET, authCode);
    }

    public String getAuthorizationUrl() {
        return mBitcasaClient.getAuthorizationUrl(CLIENT_ID);
    }

    public AccountInfo getAccountInfo() throws IOException, BitcasaException {
        return mBitcasaClient.getAccountInfo();
    }

    public BitcasaClient getClient() {
        return mBitcasaClient;
    }

    public List<FileProxy> getDirectoryFiles (String path) {
        List<FileProxy> files = new ArrayList<FileProxy>();

        try {
            FileMetaData folder = new FileMetaData();
            folder.path = path;
            if (path == null || path.trim().equals("") || path.equals("/")) {
                folder = null;
            }

            ArrayList<FileMetaData> allFiles = mBitcasaClient.getList(folder, null, 0, null);

            for (FileMetaData metaData : allFiles) {
                files.add(new BitcasaFile(metaData, path));
            }

            if (files.size() > 0 && path != null && path.equals("/")) {
                mFoldersAliases.put(files.get(0).getParentPath(), path);
            }

        } catch (Exception e) {
            throw new RuntimeException();
        }

        return files;
    }

    public void restoreToken(final BitcasaAccount bitcasaAccount, final Handler handler) {
        /*
        Extensions.runAsynk(new Runnable() {
            @Override
            public void run() {
                try {
                    setup(getAccessToken(bitcasaAccount.getAuthorizationCode()));
                    Message message = handler.obtainMessage(BitcasaLoginDialog.MSG_RESTORE_SUCCESS);
                    message.obj = bitcasaAccount;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    Message message = handler.obtainMessage(BitcasaLoginDialog.MSG_HIDE_LOADING_DIALOG);
                    message.arg1 = BitcasaLoginDialog.MSG_ARG_ERROR;
                    handler.sendMessage(message);
                }
            }
        });
        */
        try {
            setup(bitcasaAccount.getAuthorizationCode());
            Message message = handler.obtainMessage(BitcasaLoginDialog.MSG_RESTORE_SUCCESS);
            message.obj = bitcasaAccount;
            handler.sendMessage(message);
        } catch (Exception e) {
            Message message = handler.obtainMessage(BitcasaLoginDialog.MSG_HIDE_LOADING_DIALOG);
            message.arg1 = BitcasaLoginDialog.MSG_ARG_ERROR;
            handler.sendMessage(message);
        }

    }

    public NetworkAccount saveAccount(AccountInfo info, String accessCode) {
        JSONObject authData = new JSONObject();
        try {
            authData.put(ACCESS_TOKEN, accessCode);
            String name = info.getDisplay_name() + " (" + info.getId() + ")";
            long id = NetworkAccountDbAdapter.insert(name, NetworkEnum.Bitcasa.ordinal(), authData.toString());

            Cursor cursor = NetworkAccountDbAdapter.getAccountById(id);
            if (cursor != null) {
                int idxId = cursor.getColumnIndex(NetworkAccountDbAdapter.Columns.ID);
                int idxUserName = cursor.getColumnIndex(NetworkAccountDbAdapter.Columns.USER_NAME);
                int idxAuthData = cursor.getColumnIndex(NetworkAccountDbAdapter.Columns.AUTH_DATA);

                cursor.moveToNext();
                String authDataString = cursor.getString(idxAuthData);
                JSONObject data = new JSONObject(authDataString);
                return new BitcasaAccount(cursor.getLong(idxId), cursor.getString(idxUserName), data.getString(ACCESS_TOKEN));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int getAuthorizedAccountsCount() {
        return NetworkAccountDbAdapter.count(NetworkEnum.Bitcasa.ordinal());
    }

    @Override
    public List<NetworkAccount> getAuthorizedAccounts() {
        List<NetworkAccount> accounts = new ArrayList<NetworkAccount>();
        Cursor cursor = NetworkAccountDbAdapter.getAccounts(NetworkEnum.Bitcasa.ordinal());

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
                    BitcasaAccount account = new BitcasaAccount(cursor.getLong(idxId), cursor.getString(idxUserName), data.getString(ACCESS_TOKEN));
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
        return new BitcasaAccount(-1, App.sInstance.getResources().getString(R.string.btn_new), null);
    }

    @Override
    public NetworkAccount getCurrentNetworkAccount() {
        return mCurrentAccount;
    }

    @Override
    public void delete(FileProxy file) throws Exception {
        FileMetaData folder = new FileMetaData();
        folder.path = findPathId(findPathId(file.getId()));
        if (file.isDirectory()) {
            folder.type = BitcasaRESTConstants.FileType.BITCASA_TYPE_FOLDER;
        }
        mBitcasaClient.deleteFile(folder);
    }

    @Override
    public boolean createDirectory(String path) throws Exception {
        String currentDir = path.substring(0, path.lastIndexOf("/"));
        String name = path.substring(path.lastIndexOf("/") + 1, path.length());
        try {
            FileMetaData folder = new FileMetaData();
            folder.path = findPathId(currentDir);

            FileMetaData createdFolder = mBitcasaClient.addFolder(name, folder);

            if (createdFolder != null) {
                mFoldersAliases.put(createdFolder.path, path);
                return true;
            }
        } catch (Exception ignore) {
        }

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

    public static class BitcasaAccount extends NetworkAccount {

        private String mAuthorizationCode;

        public BitcasaAccount(long id, String userName, String authorizationCode) {
            mId = id;
            mUserName = userName;
            mAuthorizationCode = authorizationCode;
        }

        public String getAuthorizationCode() {
            return mAuthorizationCode;
        }
    }
}
