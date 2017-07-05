package com.openfarmanager.android.filesystem.search;

import com.openfarmanager.android.App;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.NetworkEnum;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author Vlad Namashko
 */
public class NetworkSearchFilter extends SearchFilter {

    private NetworkEnum mNetworkType;

    public NetworkSearchFilter(SearchOptions searchOptions, NetworkEnum networkType) {
        super(searchOptions);
        mNetworkType = networkType;
    }

    public Observable<FileProxy> search(String currentDirectory) {
        return io.reactivex.Observable.create(e -> {
            List<FileProxy> list = getNetworkApi().search(currentDirectory, mSearchOptions.fileMask);
            if (list != null && list.size() > 0) {
                for (FileProxy file : list) {
                    e.onNext(file);
                }
            }
            e.onComplete();
        });
    }

    private NetworkApi getNetworkApi() {
        switch (mNetworkType) {
            case Dropbox: default:
                return App.sInstance.getDropboxApi();
            case SkyDrive:
                return App.sInstance.getSkyDriveApi();
            case GoogleDrive:
                return App.sInstance.getGoogleDriveApi();
            case MediaFire:
                return App.sInstance.getMediaFireApi();
            case WebDav:
                return App.sInstance.getWebDavApi();
        }
    }

}
