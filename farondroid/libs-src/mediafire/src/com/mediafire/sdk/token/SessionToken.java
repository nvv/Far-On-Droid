package com.mediafire.sdk.token;

import com.mediafire.sdk.api.responses.UserGetSessionTokenResponse;

/**
 * a SessionToken used to make API calls (v2)
 */
public class SessionToken {
    private final String time;
    private final String pToken;
    private final String token;
    private final long sKey;
    private final String pKey;
    private final String eKey;

    private SessionToken(Builder builder) {
        token = builder.token;
        time = builder.time;
        pToken = builder.pToken;
        sKey = builder.sKey;
        pKey = builder.pKey;
        eKey = builder.eKey;
    }

    public static SessionToken makeSessionTokenFromApiResponse(UserGetSessionTokenResponse apiResponse) {
        Builder builder = new Builder(apiResponse.getSessionToken());
        builder.time(apiResponse.getTime());
        builder.secretKey(apiResponse.getSecretKey());
        builder.ekey(apiResponse.getEkey());
        builder.pkey(apiResponse.getPkey());
        builder.permanentToken(apiResponse.getPermanentToken());
        return builder.build();
    }

    public static SessionToken updateSessionToken(SessionToken token) {
        long newKey = token.sKey * 16807L;
        newKey %= 2147483647L;

        Builder builder = new Builder(token.getToken());
        builder.time(token.getTime());
        builder.secretKey(newKey);
        builder.ekey(token.getEkey());
        builder.pkey(token.getPkey());
        builder.permanentToken(token.getPermanentToken());
        return builder.build();
    }

    public final String getTime() {
        return time;
    }

    public final long getSecretKey() {
        return sKey;
    }

    public final String getPkey() {
        return pKey;
    }

    public final String getEkey() {
        return eKey;
    }

    public final String getPermanentToken() {
        return pToken;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "SessionToken{" +
                "time='" + time + '\'' +
                ", pToken='" + pToken + '\'' +
                ", token='" + token + '\'' +
                ", sKey=" + sKey +
                ", pKey='" + pKey + '\'' +
                ", eKey='" + eKey + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SessionToken that = (SessionToken) o;

        return sKey == that.sKey
                && !(eKey != null ? !eKey.equals(that.eKey) : that.eKey != null)
                && !(pKey != null ? !pKey.equals(that.pKey) : that.pKey != null)
                && !(pToken != null ? !pToken.equals(that.pToken) : that.pToken != null)
                && !(time != null ? !time.equals(that.time) : that.time != null)
                && !(token != null ? !token.equals(that.token) : that.token != null);

    }

    @Override
    public int hashCode() {
        int result = time != null ? time.hashCode() : 0;
        result = 31 * result + (pToken != null ? pToken.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (int) (sKey ^ (sKey >>> 32));
        result = 31 * result + (pKey != null ? pKey.hashCode() : 0);
        result = 31 * result + (eKey != null ? eKey.hashCode() : 0);
        return result;
    }

    public static class Builder {
        private final String token;
        private String time;
        private String pToken;
        private long sKey;
        private String pKey;
        private String eKey;

        public Builder(String tokenString) {
            token = tokenString;
        }

        public final Builder time(String value) {
            time = value;
            return this;
        }

        public final Builder permanentToken(String value) {
            pToken = value;
            return this;
        }

        public final Builder secretKey(long value) {
            sKey = value;
            return this;
        }

        public final Builder pkey(String value) {
            pKey = value;
            return this;
        }

        public final Builder ekey(String ekey) {
            eKey = ekey;
            return this;
        }

        public SessionToken build() {
            return new SessionToken(this);
        }
    }
}
