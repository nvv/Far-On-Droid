package com.openfarmanager.android.core.network.mediafire;

import android.database.Cursor;

import com.mediafire.sdk.MFApiException;
import com.mediafire.sdk.MFException;
import com.mediafire.sdk.MediaFire;
import com.mediafire.sdk.api.FileApi;
import com.mediafire.sdk.api.FolderApi;
import com.mediafire.sdk.api.responses.FileDeleteResponse;
import com.mediafire.sdk.api.responses.FileGetInfoResponse;
import com.mediafire.sdk.api.responses.FileUpdateResponse;
import com.mediafire.sdk.api.responses.FolderCreateResponse;
import com.mediafire.sdk.api.responses.FolderDeleteResponse;
import com.mediafire.sdk.api.responses.FolderGetContentsResponse;
import com.mediafire.sdk.api.responses.FolderUpdateResponse;
import com.mediafire.sdk.api.responses.data_models.File;
import com.mediafire.sdk.api.responses.data_models.Folder;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.DataStorageHelper;
import com.openfarmanager.android.core.dbadapters.NetworkAccountDbAdapter;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.MediaFireFile;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

/**
 * @author Vlad Namashko
 */
public class MediaFireApi implements NetworkApi {

    public static final String APP_ID = "46558";
    public static final String APP_KEY = "r3s0keye2wi0uucarqnuqerk4cw76h746gh3ernj";

    public final static String VERSION = "1.4";

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

    public List<FileProxy> openDirectory(String path) {
        return openDirectory(path, null);
    }

    public List<FileProxy> openDirectory(String path, String parentPath) {
        List<FileProxy> files = new ArrayList<>();

        LinkedHashMap<String, Object> query = new LinkedHashMap<>();
        query.put("response_format", "json");
        query.put("content_type", "folders");
        query.put("chunk_size", 1000);
        if (!path.equals("/")) {
            query.put("folder_key", path);
        }
        try {
            FolderGetContentsResponse response = FolderApi.getContent(mMediaFire, query, VERSION, FolderGetContentsResponse.class);

            for (Folder folder : response.getFolderContents().folders) {
                files.add(new MediaFireFile(folder, path, parentPath));
            }
            query.put("content_type", "files");

            response = FolderApi.getContent(mMediaFire, query, VERSION, FolderGetContentsResponse.class);

            for (File file : response.getFolderContents().files) {
                files.add(new MediaFireFile(file, path, parentPath));
            }

        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return files;
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
        boolean isDirectory = file.isDirectory();
        LinkedHashMap<String, Object> query = new LinkedHashMap<>();
        query.put(isDirectory ? "folder_key" : "quick_key", file.getId());
        if (isDirectory) {
            FolderApi.delete(mMediaFire, query, VERSION, FolderDeleteResponse.class);
        } else {
            FileApi.delete(mMediaFire, query, VERSION, FileDeleteResponse.class);
        }
    }

    @Override
    public String createDirectory(String baseDirectory, String newDirectoryName) throws Exception {

        LinkedHashMap<String, Object> query = new LinkedHashMap<>();
        query.put("foldername", newDirectoryName);
        query.put("parent_key", baseDirectory);
        FolderCreateResponse response = FolderApi.create(mMediaFire, query, VERSION, FolderCreateResponse.class);

        return response.getFolderKey();
    }

    @Override
    public Observable<FileProxy> search(String path, String query) {
        return null;
    }

    public MediaFire getMediaFire() {
        return mMediaFire;
    }

    @Override
    public boolean rename(FileProxy file, String newPath) throws Exception {
        boolean isDirectory = file.isDirectory();
        String name = newPath.substring(newPath.lastIndexOf("/") + 1, newPath.length());
        LinkedHashMap<String, Object> query = new LinkedHashMap<>();
        query.put(isDirectory ? "folder_key" : "quick_key", file.getId());
        query.put(isDirectory ? "foldername" : "filename", name);
        if (isDirectory) {
            FolderApi.update(mMediaFire, query, VERSION, FolderUpdateResponse.class);
        } else {
            FileApi.update(mMediaFire, query, VERSION, FileUpdateResponse.class);
        }
        return true;
    }

    public FileProxy getFileInfo(String id) {

        LinkedHashMap<String, Object> query = new LinkedHashMap<>();
        query.put("quick_key", id);

        try {
            FileGetInfoResponse response = FileApi.getInfo(mMediaFire, query, VERSION, FileGetInfoResponse.class);
            return new MediaFireFile(response.getFileInfo());
        } catch (Exception e) {
            throw NetworkException.handleNetworkException(e);
        }
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

        @Override
        public NetworkEnum getNetworkType() {
            return NetworkEnum.MediaFire;
        }
    }
}
