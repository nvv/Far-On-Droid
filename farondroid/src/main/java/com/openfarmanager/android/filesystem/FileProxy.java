package com.openfarmanager.android.filesystem;

import com.openfarmanager.android.model.Bookmark;

import java.util.List;

/**
 * File proxy object.
 * Used to represent single interface for different kind of files: files on file system, files inside archive etc.
 *
 * @author Vlad Namashko
 */
public interface FileProxy<T> {

    public String getId();

    public String getName();

    public boolean isDirectory();

    public long getSize();

    public long lastModifiedDate();

    public List<FileProxy> getChildren();

    public String getFullPath();

    public String getFullPathRaw();

    public String getParentPath();

    public boolean isUpNavigator();

    public boolean isRoot();

    public boolean isVirtualDirectory();

    public boolean isBookmark();

    public Bookmark getBookmark();

    public String getMimeType();

}
