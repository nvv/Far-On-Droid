package com.openfarmanager.android.filesystem;

import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.utils.Extensions;
import com.openfarmanager.android.utils.FileUtilsExt;

import org.apache.commons.net.ftp.FTPFile;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents FTP file.
 *
 * @author Vlad Namashko
 */
public class FtpFile implements FileProxy {

    private String mFileName;
    private boolean mIsDir;
    private long mSize;
    private long mModDate;

    private String mFullPath;
    private String mParentPath;

    public FtpFile(String currentPath, FTPFile source) {
        mFileName = source.getName();
        mIsDir = source.isDirectory();
        mSize = source.getSize();
        mModDate = source.getTimestamp().getTime().getTime();

        mFullPath = currentPath + (currentPath.endsWith("/") ? "" : "/") + source.getName();
        mParentPath = FileUtilsExt.removeLastSeparator(currentPath);
    }

    public FtpFile(String path) {
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
        return getFullPath();
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
