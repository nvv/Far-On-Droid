package com.openfarmanager.android.model;

/**
 * @author Vlad Namashko
 */
public abstract class NetworkAccount {

    protected long mId;
    protected String mUserName;

    public long getId() {
        return mId;
    }

    public String getUserName() {
        return mUserName;
    }

    public abstract NetworkEnum getNetworkType();
}
