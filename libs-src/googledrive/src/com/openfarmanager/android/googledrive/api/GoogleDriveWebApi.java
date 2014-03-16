package com.openfarmanager.android.googledrive.api;

import com.openfarmanager.android.googledrive.model.File;
import com.openfarmanager.android.googledrive.model.exceptions.CreateFolderException;
import com.openfarmanager.android.googledrive.model.exceptions.ResponseException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.openfarmanager.android.googledrive.GoogleDriveConstants.LIST_URL;
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
        List<File> files = new ArrayList<File>();
        list(files, null, path);
        return files;
    }

    public File createDirectory(String title, String parentId) {
        HttpPost httpPost = new HttpPost(LIST_URL + '?' + getAuth());
        httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
        httpPost.setHeader("Cache-Control", "no-cache");
        DefaultHttpClient httpClient = new DefaultHttpClient();
        JSONObject postData = new JSONObject();
        HttpResponse response = null;

        try {
            postData.put(TITLE, title);
            postData.put(MIME_TYPE, FOLDER_MIME_TYPE);

            JSONArray parents = new JSONArray();
            JSONObject parent = new JSONObject();
            parent.put(ID, parentId);
            postData.put(PARENTS, parents.put(parent));
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

    private void list(List<File> files, String nextPageToken, String path) throws Exception {

        if (path == null || path.trim().equals("") || path.equals("/")) {
            path = "root";
        }

        String url = LIST_URL + '?' + getAuth() + String.format("&maxResults=1000&q='%s'+in+parents+and+trashed=false", path);
        if (nextPageToken != null) {
            url += "&pageToken=" + URLEncoder.encode(nextPageToken, "UTF-8");
        }

        HttpGet httpGet = new HttpGet(url);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse response =  httpClient.execute(httpGet);

        StatusLine statusLine = response.getStatusLine();

        if (isTokenExpired(statusLine)) {
            setupToken(refreshToken(mToken));
            list(files, nextPageToken, path);
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
                list(files, pageToken, path);
            }
        }
    }
}
