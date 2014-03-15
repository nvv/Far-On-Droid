package com.openfarmanager.android.googledrive.model;

import com.openfarmanager.android.googledrive.api.Fields;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * author: Vlad Namashko
 */
public class Token {

    private String mAccessToken;
    private String mRefreshToken;

    public Token(String jsonData) throws JSONException {
        JSONObject object = new JSONObject(jsonData);
        mAccessToken = object.getString(Fields.ACCESS_TOKEN);
        mRefreshToken = object.getString(Fields.REFRESH_TOKEN);
    }

    public Token(String jsonData, String refreshToken) throws JSONException {
        JSONObject object = new JSONObject(jsonData);
        mAccessToken = object.getString(Fields.ACCESS_TOKEN);
        mRefreshToken = refreshToken;
    }

    public static Token fromLocalData(String accessToken, String refreshToken) {
        Token token = new Token();
        token.mAccessToken = accessToken;
        token.mRefreshToken = refreshToken;
        return token;
    }

    public Token() {

    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }
}
