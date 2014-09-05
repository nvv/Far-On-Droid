package com.openfarmanager.android.filesystem;

import com.openfarmanager.android.model.Bookmark;
import com.yandex.disk.client.ListItem;

import java.util.List;

/**
 * Represents YandexDisk file.
 *
 * @author Vlad Namashko
 */
public class YandexDiskFile implements FileProxy {

    private ListItem mItem;
    private String mFullPath;
    private String mParentPath;

    public YandexDiskFile(ListItem item) {
        mItem = item;

        mFullPath = mItem.getFullPath();

        mParentPath = mFullPath.substring(0, mFullPath.lastIndexOf("/") + 1);
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getName() {
        return mItem.getDisplayName();
    }

    @Override
    public boolean isDirectory() {
        return mItem.isCollection();
    }

    @Override
    public long getSize() {
        return mItem.getContentLength();
    }

    @Override
    public long lastModifiedDate() {
        return mItem.getLastUpdated();
    }

    @Override
    public List getChildren() {
        return null;
    }

    @Override
    public String getFullPath() {
        return mFullPath;
    }

    @Override
    public String getParentPath() {
        return mParentPath;
    }

    @Override
    public boolean isUpNavigator() {
        return false;
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public boolean isVirtualDirectory() {
        return false;
    }

    @Override
    public boolean isBookmark() {
        return false;
    }

    @Override
    public Bookmark getBookmark() {
        return null;
    }

    @Override
    public String getMimeType() {
        return null;
    }

    public String getPublicUrl() {
        return mItem.getPublicUrl();
    }
}
