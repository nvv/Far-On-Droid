package com.openfarmanager.android.filesystem;

import com.jcraft.jsch.ChannelSftp;
import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.utils.Extensions;
import com.openfarmanager.android.utils.FileUtilsExt;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents SFTP file.
 *
 * @author Vlad Namashko
 */
public class SftpFile implements FileProxy {

    private String mFileName;
    private boolean mIsDir;
    private long mSize;
    private long mModDate;
    private String mFullPath;
    private String mParentPath;

    public SftpFile(String currentPath, ChannelSftp.LsEntry entry) {
        mFileName = entry.getFilename();
        mIsDir = entry.getAttrs().isDir();
        mSize = entry.getAttrs().getSize();
        mModDate = entry.getAttrs().getATime() * 1000L;
        mFullPath = currentPath + (currentPath.endsWith("/") ? "" : "/") + mFileName;

        // if path ends with file separator - remove it
        mParentPath = FileUtilsExt.removeLastSeparator(currentPath);
    }

    public SftpFile(String path) {
        path = FileUtilsExt.removeLastSeparator(path);
        mFileName = FileUtilsExt.getFileName(path);
        mIsDir = true;
        mSize = 0;
        mModDate = System.currentTimeMillis();
        mFullPath = path;
        mParentPath = FileUtilsExt.getParentPath(path);
    }

    @Override
    public String getId() {
        return mFullPath;
    }

    @Override
    public String getName() {
        return mFileName;
    }

    @Override
    public boolean isDirectory() {
        return mIsDir;
    }

    @Override
    public long getSize() {
        return mSize;
    }

    @Override
    public long lastModifiedDate() {
        return mModDate;
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
    public String getFullPathRaw() {
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
