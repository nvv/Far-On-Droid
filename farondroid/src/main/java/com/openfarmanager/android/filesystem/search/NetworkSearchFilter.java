package com.openfarmanager.android.filesystem.search;

import com.annimon.stream.Stream;
import com.openfarmanager.android.App;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.filter.DateFilter;
import com.openfarmanager.android.model.NetworkEnum;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

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
//        return io.reactivex.Observable.create(e -> {
//            List<FileProxy> list = getNetworkApi().search(currentDirectory, mSearchOptions.fileMask, );
//            if (list != null && list.size() > 0) {
//                Stream.of(list).filter(file -> (file.isDirectory() ? mSearchOptions.includeFolders : mSearchOptions.includeFiles) &&
//                        DateFilter.fileFilter(file, mSearchOptions.dateAfter, mSearchOptions.dateBefore) && filterBySize(file)).forEach(e::onNext);
//            }
//            e.onComplete();
//        });


        return getNetworkApi().search(currentDirectory, mSearchOptions.fileMask).filter(file -> (file.isDirectory() ? mSearchOptions.includeFolders : mSearchOptions.includeFiles) &&
                        DateFilter.fileFilter(file, mSearchOptions.dateAfter, mSearchOptions.dateBefore) && filterBySize(file));

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
            case SFTP:
                return App.sInstance.getSftpApi();
            case FTP:
                return App.sInstance.getFtpApi();
        }
    }

}
