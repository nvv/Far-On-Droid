package com.mediafire.sdk;

class MFToken implements MediaFireToken {
    private final String sessionToken;

    protected MFToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    @Override
    public String getSessionToken() {
        return sessionToken;
    }

    @Override
    public String toString() {
        return "MFToken{" +
                "sessionToken='" + sessionToken + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MFToken mfToken = (MFToken) o;

        return getSessionToken().equals(mfToken.getSessionToken());

    }

    @Override
    public int hashCode() {
        return getSessionToken().hashCode();
    }
}
