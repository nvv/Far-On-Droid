package com.openfarmanager.android.filesystem;

import com.openfarmanager.android.model.Bookmark;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Represents file system file.
 *
 * @author Vlad Namashko
 */
public class FileSystemFile extends File implements FileProxy<File> {

    private boolean mIsBookmark;
    private boolean mIsVirtualDirectory;
    private Bookmark mBookmark;

    public FileSystemFile(String path) {
        super(path);
    }

    public FileSystemFile(File dir, String name) {
        super(dir, name);
    }

    public FileSystemFile(File dir, String name, Bookmark bookmark) {
        super(dir, name);
        mBookmark = bookmark;
        mIsBookmark = true;
    }

    public FileSystemFile(File dir, String name, boolean isVirtualDirectory) {
        super(dir, name);
        mIsVirtualDirectory = isVirtualDirectory;
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public boolean isDirectory() {
        return mIsVirtualDirectory || super.isDirectory();
    }

    @Override
    public long getSize() {
        return length();
    }

    @Override
    public long lastModifiedDate() {
        return lastModified();
    }

    @Override
    public List<File> getChildren() {
        return Arrays.asList(listFiles());
    }

    @Override
    public String getFullPath() {
        return getAbsolutePath();
    }

    @Override
    public String getParentPath() {
        return getParent();
    }

    @Override
    public boolean isUpNavigator() {
        return getName().equals("..");
    }

    @Override
    public boolean isRoot() {
        return getName().equals(".");
    }

    @Override
    public boolean isVirtualDirectory() {
        return mIsVirtualDirectory;
    }

    @Override
    public Bookmark getBookmark() {
        return mBookmark;
    }

    @Override
    public String getMimeType() {
        return null;
    }

    @Override
    public boolean isBookmark() {
        return mIsBookmark;
    }

    public void setIsBookmark() {
        mIsBookmark = true;
    }
}
