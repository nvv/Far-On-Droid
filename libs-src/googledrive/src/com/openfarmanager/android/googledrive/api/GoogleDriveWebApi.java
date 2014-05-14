package com.openfarmanager.android.googledrive.api;

import com.openfarmanager.android.googledrive.model.File;
import com.openfarmanager.android.googledrive.model.exceptions.CreateFolderException;
import com.openfarmanager.android.googledrive.model.exceptions.ResponseException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.openfarmanager.android.googledrive.GoogleDriveConstants.LIST_URL;
import static com.openfarmanager.android.googledrive.GoogleDriveConstants.UPLOAD_URL;
import static com.openfarmanager.android.googledrive.api.Fields.FOLDER_MIME_TYPE;
import static com.openfarmanager.android.googledrive.api.Fields.ID;
import static com.openfarmanager.android.googledrive.api.Fields.MIME_TYPE;
import static com.openfarmanager.android.googledrive.api.Fields.PARENTS;
import static com.openfarmanager.android.googledrive.api.Fields.TITLE;

/**
 * author: Vlad Namashko
 */
public class GoogleDriveWebApi extends Api {

    public List<File> listFiles(String path) throws Exception {
        if (path == null || path.trim().equals("") || path.equals("/")) {
            path = "root";
        }

        List<File> files = new ArrayList<File>();
        list(files, null, String.format("'%s'+in+parents+and+trashed=false", path));
        return files;
    }

    public List<File> search(String title) throws Exception {
        List<File> files = new ArrayList<File>();
        list(files, null, String.format("title+contains+'%s'+and+trashed=false", title));
        return files;
    }

    public InputStream download(String downloadLink) throws IOException {
        HttpGet httpGet = new HttpGet(downloadLink + '&' + getAuth());
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpResponse response = httpClient.execute(httpGet);

        StatusLine statusLine = response.getStatusLine();
        if (isTokenExpired(statusLine)) {
            setupToken(refreshToken(mToken));
            download(downloadLink);
        }

        if (statusLine.getStatusCode() > 200) throw new RuntimeException();

        return response.getEntity().getContent();
    }

    public void upload(String parentId, String title, java.io.File file, UploadListener listener) {
        HttpPost httpPost = new HttpPost(UPLOAD_URL + '?' + getAuth() + "&uploadType=multipart");
        httpPost.setHeader("Content-Type", String.format("multipart/related; boundary=\"%s\"", file.getName().replace(".", "_")));
        httpPost.setHeader("Cache-Control", "no-cache");
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = null;

        JSONObject postData = new JSONObject();
        try {
            setupFileNameData(title, parentId, postData);

            SimpleMultipartEntity entity = new SimpleMultipartEntity(file.getName().replace(".", "_"), listener);
            entity.addPart("meta", postData.toString());
            entity.addPart("content", file.getName(), new FileInputStream(file));

            httpPost.setEntity(entity);

            response = httpClient.execute(httpPost);

        } catch (ClientProtocolException e) {
            throw new ResponseException(0);
        } catch (IOException e) {
            throw new ResponseException(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StatusLine statusLine = response.getStatusLine();
        if (isTokenExpired(statusLine)) {
            setupToken(refreshToken(mToken));
            upload(parentId, title, file, listener);
        }

        if ((statusLine.getStatusCode() == 201 || statusLine.getStatusCode() == 200)) {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public boolean delete(String fileId) {
        HttpDelete httpDelete = new HttpDelete(LIST_URL + "/" + fileId + '?' + getAuth());
        httpDelete.setHeader("Content-Type", "application/json; charset=utf-8");
        httpDelete.setHeader("Cache-Control", "no-cache");
        DefaultHttpClient httpClient = new DefaultHttpClient();

        try {
            StatusLine statusLine = httpClient.execute(httpDelete).getStatusLine();

            if (isTokenExpired(statusLine)) {
                setupToken(refreshToken(mToken));
                return delete(fileId);
            }

            return statusLine.getStatusCode() == 204;
        } catch (Exception e) {
            return false;
        }
    }

    public File createDirectory(String title, String parentId) {
        HttpPost httpPost = new HttpPost(LIST_URL + '?' + getAuth());
        httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
        httpPost.setHeader("Cache-Control", "no-cache");
        DefaultHttpClient httpClient = new DefaultHttpClient();
        JSONObject postData = new JSONObject();
        HttpResponse response = null;

        try {
            postData.put(MIME_TYPE, FOLDER_MIME_TYPE);

            setupFileNameData(title, parentId, postData);
            httpPost.setEntity(new StringEntity(postData.toString()));

            response = httpClient.execute(httpPost);

        } catch (ClientProtocolException e) {
            throw new ResponseException(0);
        } catch (IOException e) {
            throw new ResponseException(0);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        StatusLine statusLine = response.getStatusLine();

        if (isTokenExpired(statusLine)) {
            setupToken(refreshToken(mToken));
            return createDirectory(title, parentId);
        }

        if ((statusLine.getStatusCode() == 201 || statusLine.getStatusCode() == 200)) {
            try {
                return new File(EntityUtils.toString(response.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        throw new CreateFolderException();
    }

    private void setupFileNameData(String title, String parentId, JSONObject postData) throws JSONException {
        postData.put(TITLE, title);
        JSONArray parents = new JSONArray();
        JSONObject parent = new JSONObject();
        parent.put(ID, parentId);
        postData.put(PARENTS, parents.put(parent));
    }

    public boolean rename(String fileId, String newTitle) {
        HttpPut httpPut = new HttpPut(LIST_URL + "/" + fileId + '?' + getAuth());
        httpPut.setHeader("Content-Type", "application/json; charset=utf-8");
        httpPut.setHeader("Cache-Control", "no-cache");

        try {
            JSONObject obj = new JSONObject();
            obj.put(TITLE, newTitle);
            httpPut.setEntity(new StringEntity(obj.toString()));

            DefaultHttpClient httpClient = new DefaultHttpClient();

            StatusLine statusLine = httpClient.execute(httpPut).getStatusLine();

            if (isTokenExpired(statusLine)) {
                setupToken(refreshToken(mToken));
                return delete(fileId);
            }

            return statusLine.getStatusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private void list(List<File> files, String nextPageToken, String query) throws Exception {

        String url = LIST_URL + '?' + getAuth() + "&maxResults=1000&q=" + query;
        if (nextPageToken != null) {
            url += "&pageToken=" + URLEncoder.encode(nextPageToken, "UTF-8");
        }

        HttpGet httpGet = new HttpGet(url);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse response =  httpClient.execute(httpGet);

        StatusLine statusLine = response.getStatusLine();

        if (isTokenExpired(statusLine)) {
            setupToken(refreshToken(mToken));
            list(files, nextPageToken, query);
            return;
        }

        if ((statusLine.getStatusCode() == 201 || statusLine.getStatusCode() == 200)) {
            String json = EntityUtils.toString(response.getEntity());
            JSONObject responseObject = new JSONObject(json);
            JSONArray items = responseObject.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject object = (JSONObject) items.get(i);
                try {
                    files.add(new File(object));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String pageToken = null;

            try {
                pageToken = responseObject.getString("nextPageToken");
            } catch (Exception ignore) {
            }
            if (pageToken != null) {
                list(files, pageToken, query);
            }
        }
    }

    public static interface UploadListener {
        void onProgress(int uploaded, int transferedPortion, int total);
    }
}
