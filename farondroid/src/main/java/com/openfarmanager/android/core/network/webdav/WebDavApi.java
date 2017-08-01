package com.openfarmanager.android.core.network.webdav;

import android.database.Cursor;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.DataStorageHelper;
import com.openfarmanager.android.core.dbadapters.NetworkAccountDbAdapter;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemScanner;
import com.openfarmanager.android.filesystem.WebDavFile;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.exeptions.InAppAuthException;
import com.openfarmanager.android.utils.Extensions;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.DavMethod;
import org.apache.jackrabbit.webdav.client.methods.DeleteMethod;
import org.apache.jackrabbit.webdav.client.methods.MkColMethod;
import org.apache.jackrabbit.webdav.client.methods.MoveMethod;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.client.methods.PutMethod;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

/**
 * @author Vlad Namashko
 */
public class WebDavApi implements NetworkApi {

    public static final String WEBDAV_SERVER = "server";
    public static final String WEBDAV_USER = "user";
    public static final String WEBDAV_PASSWORD = "password";

    private HttpClient mHttpClient;
    private Credentials mCredentials;
    private String mServer;

    private WebDavAccount mCurrentAccount;

    public void connectAndSave(String server, String user, String password) throws InAppAuthException {
        connect(server, user, password);

        // save account to db
        JSONObject authData = new JSONObject();
        try {
            authData.put(WEBDAV_SERVER, server);
            authData.put(WEBDAV_USER, user);
            authData.put(WEBDAV_PASSWORD, password);

            long currentAccountId = NetworkAccountDbAdapter.insert(
                    server + "(" + user + ")",
                    NetworkEnum.WebDav.ordinal(), authData.toString());

            List<NetworkAccount> accounts = parseAccounts(NetworkAccountDbAdapter.getAccountById(currentAccountId));

            if (accounts.size() == 1) {
                mCurrentAccount = (WebDavAccount) accounts.get(0);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void connect(WebDavAccount webDavAccount) throws InAppAuthException {
        mCurrentAccount = webDavAccount;
        connect(webDavAccount.getServer(), webDavAccount.getUser(), webDavAccount.getPassword());
    }

    public void connect(String server, String user, String password) {

        HttpConnectionManagerParams connParam = new HttpConnectionManagerParams();
        connParam.setMaxTotalConnections(32);
        MultiThreadedHttpConnectionManager connectionManager =new MultiThreadedHttpConnectionManager();
        connectionManager.setParams(connParam);
        mHttpClient = new HttpClient(connectionManager);
        mServer = server;

        if (!Extensions.isNullOrEmpty(user)) {
            mCredentials = new UsernamePasswordCredentials(user, password);
            mHttpClient.getState().setCredentials(AuthScope.ANY, mCredentials);
        }

        try {
            DavMethod method = new PropFindMethod(mServer,
                    DavConstants.PROPFIND_BY_PROPERTY,
                    null,
                    DavConstants.DEPTH_0);
            mHttpClient.executeMethod(method);
            method.getResponseBodyAsMultiStatus();
            method.releaseConnection();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new InAppAuthException(App.sInstance.getString(R.string.error_wrong_smb_host));
        } catch (DavException e) {
            if (e.getMessage().equals("Unauthorized")) {
                throw new InAppAuthException(App.sInstance.getString(R.string.error_smb_wrong_credentials));
            } else {
                throw new InAppAuthException(App.sInstance.getString(R.string.error_ftp_io));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InAppAuthException(App.sInstance.getString(R.string.error_ftp_io));
        }
    }

    public List<FileProxy> getDirectoryFiles(String fullPath) {
        List<FileProxy> files = new ArrayList<>();

        try {
            DavMethod method = new PropFindMethod(mServer + escape(fullPath),
                    DavConstants.PROPFIND_ALL_PROP_INCLUDE,
                    new DavPropertyNameSet(),
                    DavConstants.DEPTH_1);
            mHttpClient.executeMethod(method);

            MultiStatus multiStatus = method.getResponseBodyAsMultiStatus();
            MultiStatusResponse[] responses = multiStatus.getResponses();

            for (MultiStatusResponse response : responses) {
                String path = response.getHref();
                try {
                    path = URLDecoder.decode(path, "UTF-8");
                } catch (Exception e) {
                    continue;
                }
                if (!path.equals(fullPath) && !path.equals(fullPath + "/")) {
                    files.add(new WebDavFile(response, fullPath));
                }
            }
            method.releaseConnection();
            FileSystemScanner.sInstance.sort(files);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
    }

    private String escape(String string) {
        return string.replace(" ", "%20");
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
                    WebDavAccount account = new WebDavAccount(cursor.getLong(idxId), cursor.getString(idxUserName),
                            data.getString(WEBDAV_SERVER), data.getString(WEBDAV_USER), data.getString(WEBDAV_PASSWORD));
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
    public int getAuthorizedAccountsCount() {
        return NetworkAccountDbAdapter.count(NetworkEnum.WebDav.ordinal());
    }

    @Override
    public List<NetworkAccount> getAuthorizedAccounts() {
        return parseAccounts(NetworkAccountDbAdapter.getAccounts(NetworkEnum.WebDav.ordinal()));
    }

    @Override
    public NetworkAccount newAccount() {
        return new WebDavAccount(-1, App.sInstance.getResources().getString(com.openfarmanager.android.R.string.btn_new),
                null, null, null);
    }

    @Override
    public NetworkAccount getCurrentNetworkAccount() {
        return mCurrentAccount;
    }

    @Override
    public void delete(FileProxy file) throws Exception {
        DavMethod method = new DeleteMethod(mServer + escape(file.getFullPath()));
        mHttpClient.executeMethod(method);
        method.releaseConnection();
    }

    @Override
    public String createDirectory(String baseDirectory, String newDirectoryName) throws Exception {
        String path  = (baseDirectory + "/" + escape(newDirectoryName));
        DavMethod method = new MkColMethod(mServer + path);
        mHttpClient.executeMethod(method);
        method.releaseConnection();
        return path;
    }

    @Override
    public Observable<FileProxy> search(String path, String query) {
        return null;
    }

    @Override
    public boolean rename(FileProxy srcFile, String s) throws Exception {
        DavMethod method = new MoveMethod(mServer + escape(srcFile.getFullPath()), mServer + escape(s), true);
        mHttpClient.executeMethod(method);
        method.releaseConnection();
        return true;
    }

    public void copyToWebDav(File source, InputStreamRequestEntity.OutputStreamListener listener, String destination, String fileName) throws Exception {
        PutMethod method = new PutMethod(mServer + escape(destination + fileName));
        InputStreamRequestEntity entity = new InputStreamRequestEntity(source);
        entity.setListener(listener);
        method.setRequestEntity(entity);
        mHttpClient.executeMethod(method);

        method.releaseConnection();
    }

    public InputStream getFromWebDav(String filePath) throws Exception {
        GetMethod getMethod = new GetMethod(mServer + escape(filePath));
        mHttpClient.executeMethod(getMethod);
        if (getMethod.getStatusCode() == 200 || getMethod.getStatusCode() == 201) {
            return getMethod.getResponseBodyAsStream();
        }
        getMethod.releaseConnection();
        return null;
    }

    public static class WebDavAccount extends NetworkAccount {

        private String mServer;
        private String mUser;
        private String mPassword;

        public WebDavAccount(long id, String userName, JSONObject data) throws JSONException {
            this(id, userName, data.getString(WEBDAV_SERVER), data.getString(WEBDAV_USER), data.getString(WEBDAV_PASSWORD));
        }

        public WebDavAccount(long id, String userName, String domain, String user, String password) {
            mId = id;
            mUserName = userName;
            mServer = domain;
            mUser = user;
            mPassword = password;
        }

        public String getServer() {
            return mServer;
        }

        public String getUser() {
            return mUser;
        }

        public String getPassword() {
            return mPassword;
        }

        @Override
        public NetworkEnum getNetworkType() {
            return NetworkEnum.WebDav;
        }
    }
}
