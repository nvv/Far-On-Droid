package com.openfarmanager.android.googledrive.api;

import com.openfarmanager.android.googledrive.model.About;
import com.openfarmanager.android.googledrive.model.File;
import com.openfarmanager.android.googledrive.model.Token;
import com.openfarmanager.android.googledrive.model.exceptions.CreateFolderException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private String setupQuery(String path) {
        switch (path) {
            case File.SHARED_FOLDER_ID:
                return "sharedWithMe";
            case File.STARRED_FOLDER_ID:
                return "starred=true";
            default:
                return String.format("'%s'+in+parents+and+trashed=false", path);
        }
    }

    public List<File> listFiles(String path) throws Exception {
        if (path == null || path.trim().equals("") || path.equals("/")) {
            path = "root";
        }

        List<File> files = new ArrayList<File>();
        list(files, null, setupQuery(path));
        return files;
    }

    public List<File> search(String title) throws Exception {
        List<File> files = new ArrayList<File>();
        list(files, null, String.format("title+contains+'%s'+and+trashed=false", title));
        return files;
    }

    public String getDownloadLink(String link) {
        return link + '&' + getAuth();
    }

    public InputStream download(String downloadLink) throws IOException {
        URL url = new URL(downloadLink + '&' + getAuth());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        int statusCode = connection.getResponseCode();
        if (isTokenExpired(statusCode, connection.getResponseMessage())) {
            setupToken(refreshToken(mToken));
            download(downloadLink);
        }

        if (statusCode > 200) throw new RuntimeException();

        return connection.getInputStream();
    }

    public void upload(String parentId, String title, java.io.File file, UploadListener listener) {
        try {
            JSONObject postData = new JSONObject();
            setupFileNameData(title, parentId, postData);

            MultipartUtility multipartUtility = new MultipartUtility(new URL(UPLOAD_URL + '?' + getAuth() + "&uploadType=multipart"));
            try {
                multipartUtility.addFormField("meta", postData.toString(), "application/json");
                multipartUtility.addFilePart("content", file, listener);

                int statusCode = multipartUtility.doRequest();
                String message = multipartUtility.getResponseMessage();

                if (isTokenExpired(statusCode, message)) {
                    setupToken(refreshToken(mToken));
                    upload(parentId, title, file, listener);
                }

            } finally {
                multipartUtility.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean delete(String fileId) {
        try {
            HttpURLConnection connection = createConnection(new URL(LIST_URL + "/" + fileId + '?' + getAuth()));
            connection.setRequestMethod(METHOD_DELETE);
            int responseCode = connection.getResponseCode();
            if (responseCode == 403) {
                return delete(fileId);
            }
            return responseCode >= 200 && responseCode <= 204;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public File createDirectory(String title, String parentId) {
        try {
            HttpURLConnection connection = createConnection(new URL(LIST_URL + '?' + getAuth()));
            connection.setRequestMethod(METHOD_POST);

            JSONObject postData = new JSONObject();
            postData.put(MIME_TYPE, FOLDER_MIME_TYPE);
            setupFileNameData(title, parentId, postData);

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(postData.toString());
            out.close();

            int statusCode = connection.getResponseCode();

            if (isTokenExpired(statusCode, connection.getResponseMessage())) {
                setupToken(refreshToken(mToken));
                return createDirectory(title, parentId);
            }

            if (statusCode == 201 || statusCode == 200) {
                return new File(streamToString(connection.getInputStream()));
            }

            throw new CreateFolderException();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean rename(String fileId, String newTitle) {
        try {
            JSONObject obj = new JSONObject();
            obj.put(TITLE, newTitle);
            return updateData(fileId, obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateData(String fileId, String data) {
        try {
            HttpURLConnection connection = createConnection(new URL(LIST_URL + "/" + fileId + '?' + getAuth()));
            connection.setRequestMethod(METHOD_PUT);

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(data);
            out.close();

            int statusCode = connection.getResponseCode();

            if (isTokenExpired(statusCode, connection.getResponseMessage())) {
                setupToken(refreshToken(mToken));
                return updateData(fileId, data);
            }

            return statusCode == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void list(List<File> files, String nextPageToken, String query) throws Exception {

        String url = LIST_URL + '?' + getAuth() + "&maxResults=1000&q=" + query;
        if (nextPageToken != null) {
            url += "&pageToken=" + URLEncoder.encode(nextPageToken, "UTF-8");
        }

        URL urlConnection = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();

        int statusCode = connection.getResponseCode();
        if (isTokenExpired(statusCode, connection.getResponseMessage())) {
            setupToken(refreshToken(mToken));
            list(files, nextPageToken, query);
            return;
        }

        if (statusCode == 201 || statusCode == 200) {
            String json = streamToString(connection.getInputStream());
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

    public File getFile(String fileId) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(LIST_URL + "/" + fileId + "?" + getAuth()).openConnection();
        connection.setRequestProperty("Cache-Control", "no-cache");
        int responseCode = connection.getResponseCode();
        if (isTokenExpired(responseCode, connection.getResponseMessage())) {
            setupToken(refreshToken(mToken));
            return getFile(fileId);
        }

        if (responseCode == 201 || responseCode == 200) {
            return new File(streamToString(connection.getInputStream()));
        }

        return null;

    }

    private HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Cache-Control", "no-cache");
        return connection;
    }

    private void setupFileNameData(String title, String parentId, JSONObject postData) throws JSONException {
        postData.put(TITLE, title);
        JSONArray parents = new JSONArray();
        JSONObject parent = new JSONObject();
        parent.put(ID, parentId);
        postData.put(PARENTS, parents.put(parent));
    }

    public static interface UploadListener {
        void onProgress(int uploaded, int transferedPortion, int total);
    }
}
