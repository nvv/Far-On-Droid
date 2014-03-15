package com.openfarmanager.android.googledrive.api;

import com.openfarmanager.android.googledrive.model.File;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.openfarmanager.android.googledrive.GoogleDriveConstants.ABOUT_URL;
import static com.openfarmanager.android.googledrive.GoogleDriveConstants.LIST_URL;

/**
 * author: Vlad Namashko
 */
public class GoogleDriveWebApi extends Api {

    public List<File> listFiles(String path) throws Exception {
        List<File> files = new ArrayList<File>();
        list(files, null, path);
        return files;
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
