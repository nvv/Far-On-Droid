package com.openfarmanager.android.googledrive.api;

import com.openfarmanager.android.googledrive.R;
import com.openfarmanager.android.googledrive.model.About;
import com.openfarmanager.android.googledrive.model.Token;
import com.openfarmanager.android.googledrive.model.exceptions.ResponseException;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.openfarmanager.android.googledrive.GoogleDriveConstants.ABOUT_URL;
import static com.openfarmanager.android.googledrive.GoogleDriveConstants.AUTH_URL;
import static com.openfarmanager.android.googledrive.GoogleDriveConstants.CLIENT_ID;
import static com.openfarmanager.android.googledrive.GoogleDriveConstants.REDIRECT_URL;
import static com.openfarmanager.android.googledrive.GoogleDriveConstants.TOKEN_URL;
import static com.openfarmanager.android.googledrive.api.Fields.ACCESS_TOKEN;
import static com.openfarmanager.android.googledrive.api.Fields.AUTHORIZATION_CODE;
import static com.openfarmanager.android.googledrive.api.Fields.CODE;
import static com.openfarmanager.android.googledrive.api.Fields.GRANT_TYPE;
import static com.openfarmanager.android.googledrive.api.Fields.REFRESH_TOKEN;

/**
 * author: Vlad Namashko
 */
public class Api {

    protected static final String METHOD_DELETE = "DELETE";
    protected static final String METHOD_PUT = "PUT";
    protected static final String METHOD_POST = "POST";

    private static final String AUTH_CODE_URL = AUTH_URL + "?" +
            "client_id=" + CLIENT_ID + "&" +
            "response_type=code&" +
            "scope=openid%20https://www.googleapis.com/auth/drive&" +
            "redirect_uri=" + REDIRECT_URL;

    protected Token mToken;

    public String getAuthCodeUrl() {
        return AUTH_CODE_URL;
    }

    public void setupToken(Token token) {
        mToken = token;
    }

    public Token refreshToken(final Token token) {
        try {
            HttpURLConnection connection = prepareTokenRequest(REFRESH_TOKEN, new HashMap<String, String>() {{
                put(REFRESH_TOKEN, token.getRefreshToken());
            }});

            int responseCode = connection.getResponseCode();
            if (responseCode == 201 || responseCode == 200) {
                return new Token(streamToString(connection.getInputStream()), token.getRefreshToken());
            }
        } catch (JSONException e) {
            throw new ResponseException(R.string.response_error);
        }
        // TODO: Exceptions
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Token getAuthToken(String url) {
        final String code = extractAuthCode(url);

        try {
            HttpURLConnection connection = prepareTokenRequest(AUTHORIZATION_CODE, new HashMap<String, String>() {{
                put(CODE, code);
                put(Fields.REDIRECT_URI, REDIRECT_URL);
            }});

            int responseCode = connection.getResponseCode();
            if (responseCode == 201 || responseCode == 200) {
                return new Token(streamToString(connection.getInputStream()));
            }


        } catch (JSONException e) {
            throw new ResponseException(R.string.response_error);
        }
        // TODO: Exceptions
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public About getAbout(Token token) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(ABOUT_URL + "?" + getAuth(token)).openConnection();
        connection.setRequestProperty("Cache-Control", "no-cache");
        int responseCode = connection.getResponseCode();
        if (isTokenExpired(responseCode, connection.getResponseMessage())) {
            setupToken(refreshToken(mToken));
            return getAbout(mToken);
        }

        if (responseCode == 201 || responseCode == 200) {
            return new About(streamToString(connection.getInputStream()));
        }

        return null;
    }

    private HttpURLConnection prepareTokenRequest(String grantType, HashMap<String, String> params) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(TOKEN_URL).openConnection();
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestMethod(METHOD_POST);

        params.put(GRANT_TYPE, grantType);
        params.put(Fields.CLIENT_ID, CLIENT_ID);

        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(getPostDataString(params));

        writer.flush();
        writer.close();
        MultipartUtility.closeStream(writer);
        MultipartUtility.closeStream(os);

        return connection;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    protected String getAuth() {
        return getAuth(mToken);
    }

    private String getAuth(final Token token) {
        try {
            return getPostDataString(new HashMap<String, String>() {{
                put(ACCESS_TOKEN, token.getAccessToken());
            }});
        } catch (Exception e) {
            return null;
        }
    }

    public String extractAuthCode(String url) {
        int pos = url.indexOf("code=");
        return url.substring(pos + 5, pos + url.indexOf("&", pos) - pos);
    }

    protected boolean isTokenExpired(int statusCode, String message) {
        return statusCode == 401 && message.equals("Unauthorized");
    }

    protected String streamToString(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }
}
