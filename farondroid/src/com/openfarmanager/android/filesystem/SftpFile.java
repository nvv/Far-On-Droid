package com.openfarmanager.android.filesystem;

import com.jcraft.jsch.ChannelSftp;
import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.utils.Extensions;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents SFTP file.
 *
 * @author Vlad Namashko
 */
public class SftpFile implements FileProxy {

    private ChannelSftp.LsEntry mEntry;
    private String mFullPath;
    private String mParentPath;

    public SftpFile(String currentPath, ChannelSftp.LsEntry entry) {
        mEntry = entry;

        mFullPath = currentPath + (currentPath.endsWith("/") ? "" : "/") + mEntry.getFilename();
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
        return mEntry.getFilename();
    }

    @Override
    public boolean isDirectory() {
        return mEntry.getAttrs().isDir();
    }

    @Override
    public long getSize() {
        return mEntry.getAttrs().getSize();
    }

    @Override
    public long lastModifiedDate() {
        return mEntry.getAttrs().getATime() * 1000L;
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
