package com.openfarmanager.android.filesystem;

import com.openfarmanager.android.model.Bookmark;

import java.util.List;

/**
 * @author Vlad Namashko
 */
public class FakeFile implements FileProxy {

    private String mName;
    private String mParentPath;
    private boolean mIsRoot;

    public FakeFile(String name, String parentPath, boolean isRoot) {
        mName = name;
        mParentPath = parentPath;
        mIsRoot = isRoot;
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public long lastModifiedDate() {
        return 0;
    }

    @Override
    public List getChildren() {
        return null;
    }

    @Override
    public String getFullPath() {
        return null;
    }

    @Override
    public String getParentPath() {
        return mParentPath;
    }

    @Override
    public boolean isUpNavigator() {
        return "..".equals(mName);
    }

    @Override
    public boolean isRoot() {
        return mIsRoot;
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
}
