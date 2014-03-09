package com.openfarmanager.android.googledrive.api;

import com.openfarmanager.android.googledrive.R;
import com.openfarmanager.android.googledrive.model.About;
import com.openfarmanager.android.googledrive.model.exceptions.ResponseException;
import com.openfarmanager.android.googledrive.model.Token;
import com.openfarmanager.android.googledrive.model.exceptions.TokenExpiredException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.openfarmanager.android.googledrive.GoogleDriveConstants.ABOUT_URL;
import static com.openfarmanager.android.googledrive.GoogleDriveConstants.AUTH_URL;
import static com.openfarmanager.android.googledrive.GoogleDriveConstants.CLIENT_ID;
import static com.openfarmanager.android.googledrive.GoogleDriveConstants.REDIRECT_URL;
import static com.openfarmanager.android.googledrive.GoogleDriveConstants.TOKEN_URL;

import static com.openfarmanager.android.googledrive.api.Fields.*;
import static com.openfarmanager.android.googledrive.api.Fields.CODE;

/**
 * author: Vlad Namashko
 */
public class Api {

    private static final String AUTH_CODE_URL = AUTH_URL + "?" +
            "client_id=" + CLIENT_ID + "&" +
            "response_type=code&" +
            "scope=openid%20https://www.googleapis.com/auth/drive.file&" +
            "redirect_uri=" + REDIRECT_URL;

    public static Api sInstance;

    static {
        sInstance = new Api();
    }

    public String getAuthCodeUrl() {
        return AUTH_CODE_URL;
    }

    public Token refreshToken(final Token token) {
        try {
            HttpResponse response = prepareTokenRequest(REFRESH_TOKEN, new ArrayList<BasicNameValuePair>() {{
                    add(new BasicNameValuePair(REFRESH_TOKEN, token.getRefreshToken()));
                }});

            if ((response.getStatusLine().getStatusCode() == 201 ||
                    response.getStatusLine().getStatusCode() == 200)) {
                return new Token(EntityUtils.toString(response.getEntity()), token.getRefreshToken());
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
            HttpResponse response = prepareTokenRequest(AUTHORIZATION_CODE, new ArrayList<BasicNameValuePair>() {{
                add(new BasicNameValuePair(CODE, code));
                add(new BasicNameValuePair(Fields.REDIRECT_URI, REDIRECT_URL));
            }});

            if ((response.getStatusLine().getStatusCode() == 201 ||
                    response.getStatusLine().getStatusCode() == 200)) {
                return new Token(EntityUtils.toString(response.getEntity()));
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

        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair(ACCESS_TOKEN, token.getAccessToken()));
        String paramString = URLEncodedUtils.format(params, "utf-8");

        HttpGet httpGet = new HttpGet(ABOUT_URL + "?" + paramString);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse response =  httpClient.execute(httpGet);

        StatusLine statusLine = response.getStatusLine();
        if ((statusLine.getStatusCode() == 201 || statusLine.getStatusCode() == 200)) {
            return new About(EntityUtils.toString(response.getEntity()));
        } else if (statusLine.getStatusCode() == 401 && statusLine.getReasonPhrase().equals("Unauthorized")) {
            throw new TokenExpiredException();
        }

        return null;
    }

    private HttpResponse prepareTokenRequest(String grantType, ArrayList<BasicNameValuePair> extraParams) throws Exception {
        HttpPost httpPost = new HttpPost(TOKEN_URL);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        httpPost.setHeader("Cache-Control", "no-cache");
        List<NameValuePair> params = new ArrayList<NameValuePair>(4);
        params.add(new BasicNameValuePair(GRANT_TYPE, grantType));
        params.add(new BasicNameValuePair(Fields.CLIENT_ID, CLIENT_ID));
        params.addAll(extraParams);
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        return httpClient.execute(httpPost);
    }

    public String extractAuthCode(String url) {
        int pos = url.indexOf("code=");
        return url.substring(pos + 5, pos + url.indexOf("&", pos) - pos);
    }


}
