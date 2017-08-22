package com.openfarmanager.android.filesystem;

import com.annimon.stream.Stream;
import com.openfarmanager.android.model.Bookmark;

import java.io.File;
import java.util.ArrayList;
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
    private List<FileProxy> mChildren;

    public FileSystemFile(String path) {
        super(path);
    }

    public FileSystemFile(File file) {
        super(file.getParent(), file.getName());
    }

    public FileSystemFile(File dir, String name) {
        super(dir, name);
    }

    public FileSystemFile(File dir, String name, Bookmark bookmark) {
        super(dir, name);
        mBookmark = bookmark;
        mIsBookmark = true;

        File[] children = listFiles();
        if (children != null) {
            mChildren = new ArrayList<>(children.length);
            Stream.of(children).forEach(child -> mChildren.add(new FileSystemFile(child.getAbsolutePath())));
        } else {
            mChildren = new ArrayList<>();
        }
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
    public List<FileProxy> getChildren() {
        return mChildren;
    }

    @Override
    public String getFullPath() {
        return getAbsolutePath();
    }

    @Override
    public String getFullPathRaw() {
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
