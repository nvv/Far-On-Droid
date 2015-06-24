package com.mediafire.sdk.token;

import com.mediafire.sdk.api.responses.UserGetActionTokenResponse;

/**
 * an ActionToken used to make certain types of API calls
 */
public class ActionToken {
    private final String tokenString;
    private final long expirationMillis;

    public ActionToken(String tokenString, long expirationMillis) {
        this.tokenString = tokenString;
        this.expirationMillis = expirationMillis;
    }

    /**
     * Gets the expiration time of the token
     * @return long expiration
     */
    public long getExpiration() {
        return expirationMillis;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationMillis;
    }

    public boolean isExpiringWithinMillis(long millis) {
        return System.currentTimeMillis() > expirationMillis - millis;
    }

    public String getToken() {
        return tokenString;
    }

    public static ActionToken makeActionTokenFromApiResponse(UserGetActionTokenResponse apiResponse, long expirationTime) {
        return new ActionToken(apiResponse.getActionToken(), expirationTime);
    }

    @Override
    public String toString() {
        return "ActionToken{" +
                "tokenString='" + tokenString + '\'' +
                ", expirationMillis=" + expirationMillis +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActionToken that = (ActionToken) o;

        return expirationMillis == that.expirationMillis && !(tokenString != null ? !tokenString.equals(that.tokenString) : that.tokenString != null);

    }

    @Override
    public int hashCode() {
        int result = tokenString != null ? tokenString.hashCode() : 0;
        result = 31 * result + (int) (expirationMillis ^ (expirationMillis >>> 32));
        return result;
    }
}
