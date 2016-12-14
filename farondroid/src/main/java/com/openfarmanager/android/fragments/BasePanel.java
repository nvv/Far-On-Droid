package com.openfarmanager.android.fragments;

import android.support.v4.app.Fragment;
import com.openfarmanager.android.App;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Vlad Namashko
 */
public abstract class BasePanel extends Fragment implements Serializable {

    private Queue<Runnable> mPendingCommands = new LinkedList<Runnable>();
    protected boolean mIsInitialized;

    protected void addToPendingList(Runnable runnable) {
        mPendingCommands.add(runnable);
    }

    protected void postInitialization() {
        mIsInitialized = true;

        // post execute for pending tasks.
        while (!mPendingCommands.isEmpty()) {
            getActivity().runOnUiThread(mPendingCommands.remove());
        }
    }

    public boolean isInitialized() {
        return mIsInitialized;
    }

    /**
     * getstring using Application instance instead of Activity, which throw exception.
     *
     * @param resId Resource id for the string
     */
    public final String getSafeString(int resId) {
        return App.sInstance.getString(resId);
    }

    /**
     * getstring using Application instance instead of Activity, which throw exception.
     *
     * @param resId Resource id for the string
     */
    public final String getSafeString(int resId, Object... formatArgs) {
        return App.sInstance.getString(resId, formatArgs);
    }

    public boolean isFileSystemPanel() {
        return false;
    }
}
