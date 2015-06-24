package com.mediafire.sdk.config;

/**
 * a generic store used by session/action requestres
 * @param <T>
 */
public interface MFStore<T> {
    /**
     * whether T is available in the store
     * @return true if T is available
     */
    public boolean available();

    /**
     * gets a T from the store
     * @return T
     */
    public T get();

    /**
     * puts a T into the store
     * @param t T
     */
    public void put(T t);

    /**
     * clears the store of all T elements
     */
    public void clear();

    /**
     * gets the available count of T in the store
     * @return int
     */
    public int getAvailableCount();
}
