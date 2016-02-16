package com.openfarmanager.android.filesystem;

import com.openfarmanager.android.model.Bookmark;

import java.util.List;

/**
 * @author Vlad Namashko
 */
public class FakeFile implements FileProxy {

    private String mId;
    private String mName;
    private String mParentPath;
    private String mFullPath;
    private String mFullPathRaw;
    private boolean mIsRoot;

    public FakeFile(String id, String name, String parentPath, String fullPathRaw, boolean isRoot) {
        mId = id;
        mName = name;
        mParentPath = parentPath;
        mFullPath = parentPath + name;
        mFullPathRaw = fullPathRaw;
        mIsRoot = isRoot;
    }

    public FakeFile(String id, String name) {
        mId = id;
        mName = name;
        mFullPath = id;
        mFullPathRaw = name;
        mParentPath = name;
    }

    @Override
    public String getId() {
        return mId;
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
        return mFullPath;
    }

    @Override
    public String getFullPathRaw() {
        return mFullPathRaw;
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

    @Override
    public String getMimeType() {
        return null;
    }
}
