package com.openfarmanager.android.filesystem.actions.multi.network;

import android.content.Context;

import com.openfarmanager.android.App;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.filesystem.actions.multi.MultiActionTask;
import com.openfarmanager.android.model.NetworkEnum;

import java.io.File;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public abstract class NetworkActionMultiTask extends MultiActionTask {

    protected NetworkEnum mNetworkType;

    public NetworkActionMultiTask(Context context, OnActionListener listener, List<File> items, NetworkEnum networkType) {
        super(context, listener, items);
        mNetworkType = networkType;
    }

    protected NetworkActionMultiTask() {
    }

    protected NetworkApi getApi() {
        return App.sInstance.getNetworkApi(mNetworkType);
    }
}
