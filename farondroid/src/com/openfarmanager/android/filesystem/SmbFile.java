package com.openfarmanager.android.filesystem;

import com.openfarmanager.android.App;
import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.utils.Extensions;

import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbException;

/**
 * Represents SMB file.
 *
 * @author Vlad Namashko
 */
public class SmbFile implements FileProxy {

    private String mName;
    private boolean mIsDirectory;
    private long mSize;
    private long mLastModified;
    private String mFullPath;
    private String mParentPath;

    public SmbFile(jcifs.smb.SmbFile smbFile, String hostName) throws SmbException {
        mName = smbFile.getName();
        mIsDirectory = smbFile.isDirectory();
        mSize = smbFile.length();
        mLastModified = smbFile.lastModified();

        mFullPath = smbFile.getPath();
        String prefix = "smb://" + hostName;

        if (mFullPath.startsWith(prefix)) {
            mFullPath = mFullPath.substring(prefix.length());
        }

        mParentPath = smbFile.getParent();
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
        return mIsDirectory;
    }

    @Override
    public long getSize() {
        return mSize;
    }

    @Override
    public long lastModifiedDate() {
        return mLastModified;
    }

    @Override
    public List getChildren() {
        return new ArrayList();
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
        return getName().equals("..") && Extensions.isNullOrEmpty(mParentPath);
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
