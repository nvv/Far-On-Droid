package com.openfarmanager.android.filesystem;

import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.utils.Extensions;

import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPFile;

/**
 * Represents FTP file.
 *
 * @author Vlad Namashko
 */
public class FtpFile implements FileProxy {

    private FTPFile mFtpFile;
    private String mFullPath;
    private String mParentPath;

    public FtpFile(String currentPath, FTPFile source) {
        mFtpFile = source;

        mFullPath = currentPath + (currentPath.endsWith("/") ? "" : "/") + source.getName();
        mParentPath = mFullPath.substring(0, mFullPath.lastIndexOf("/") + 1);

        if (mParentPath.endsWith("/") && mParentPath.length() > 1) {
            mParentPath = mParentPath.substring(0, mParentPath.length() - 1);
        }
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getName() {
        return mFtpFile.getName();
    }

    @Override
    public boolean isDirectory() {
        return mFtpFile.getType() == FTPFile.TYPE_DIRECTORY;
    }

    @Override
    public long getSize() {
        return mFtpFile.getSize();
    }

    @Override
    public long lastModifiedDate() {
        return mFtpFile.getModifiedDate().getTime();
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
}
