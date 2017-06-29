package com.openfarmanager.android.filesystem;

import com.openfarmanager.android.core.archive.ArchiveScanner;
import com.openfarmanager.android.model.Bookmark;

import java.util.List;

/**
 * Represents archive system file.
 *
 * @author Vlad Namashko
 */
public class ArchiveFile extends ArchiveScanner.File implements FileProxy<ArchiveScanner.File>  {

    public ArchiveFile(ArchiveScanner.File file) {
        super(file);
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
        return super.isDirectory();
    }

    @Override
    public long getSize() {
        return super.getSize();
    }

    @Override
    public long lastModifiedDate() {
        return 0;
    }

    @Override
    public List<FileProxy> getChildren() {
        return super.getChildren();
    }

    @Override
    public String getFullPath() {
        return super.getFullPath();
    }

    @Override
    public String getFullPathRaw() {
        return super.getFullPath();
    }

    @Override
    public String getParentPath() {
        return getParent().getFullPath();
    }

    @Override
    public boolean isUpNavigator() {
        return super.isUpNavigator();
    }

    @Override
    public boolean isRoot() {
        return super.isRoot();
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
