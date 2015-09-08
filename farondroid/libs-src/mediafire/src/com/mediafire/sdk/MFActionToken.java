package com.mediafire.sdk;

public class MFActionToken extends MFToken implements MediaFireActionToken {
    private final int type;
    private final long requestTime;
    private final int lifespan;

    protected MFActionToken(String sessionToken, int type, long requestTime, int lifespan) {
        super(sessionToken);
        this.type = type;
        this.requestTime = requestTime;
        this.lifespan = lifespan;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public long getRequestTime() {
        return requestTime;
    }

    @Override
    public int getLifespan() {
        return lifespan;
    }

    @Override
    public String toString() {
        return "MFActionToken{" +
                "type=" + type +
                ", requestTime=" + requestTime +
                ", lifespan=" + lifespan +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MFActionToken that = (MFActionToken) o;

        if (getType() != that.getType()) return false;
        if (getRequestTime() != that.getRequestTime()) return false;
        return getLifespan() == that.getLifespan();

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getType();
        result = 31 * result + (int) (getRequestTime() ^ (getRequestTime() >>> 32));
        result = 31 * result + getLifespan();
        return result;
    }
}
