package com.mediafire.sdk;

public class MFSessionToken extends MFToken implements MediaFireSessionToken {
    private final String time;
    private long secretKey;
    private final String pkey;
    private final String ekey;

    public MFSessionToken(String sessionToken, String time, long secretKey, String pkey, String ekey) {
        super(sessionToken);
        this.time = time;
        this.secretKey = secretKey;
        this.pkey = pkey;
        this.ekey = ekey;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public long getSecretKey() {
        return secretKey;
    }

    @Override
    public String getPkey() {
        return pkey;
    }

    @Override
    public String getEkey() {
        return ekey;
    }

    @Override
    public void update() {
        long newKey = secretKey * 16807L;
        newKey %= 2147483647L;
        this.secretKey = newKey;
    }

    @Override
    public String toString() {
        return "MFSessionToken{" +
                "time='" + time + '\'' +
                ", secretKey=" + secretKey +
                ", pkey='" + pkey + '\'' +
                ", ekey='" + ekey + '\'' +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MFSessionToken that = (MFSessionToken) o;

        if (getSecretKey() != that.getSecretKey()) return false;
        if (!getTime().equals(that.getTime())) return false;
        if (!getPkey().equals(that.getPkey())) return false;
        return getEkey().equals(that.getEkey());

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getTime().hashCode();
        result = 31 * result + (int) (getSecretKey() ^ (getSecretKey() >>> 32));
        result = 31 * result + getPkey().hashCode();
        result = 31 * result + getEkey().hashCode();
        return result;
    }
}
