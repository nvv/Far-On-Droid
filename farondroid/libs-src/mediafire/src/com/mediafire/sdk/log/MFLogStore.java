package com.mediafire.sdk.log;

import java.util.List;

/**
 * Created by Chris on 5/26/2015.
 */
public interface MFLogStore<T extends MFLog> {
    /**
     * deletes all T from store
     * @return number of deleted items
     */
    public long deleteAll();

    /**
     * gets the count of T from store
     * @return the number of T items
     */
    public long getCount();

    /**
     * adds a T to the store
     * @param t the T to add
     * @return the index or id of T that was added
     */
    public long addLog(T t);
}
