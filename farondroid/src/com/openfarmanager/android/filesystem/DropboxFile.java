package com.openfarmanager.android.filesystem;

import com.dropbox.client2.DropboxAPI;
import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.utils.CustomFormatter;

import static com.openfarmanager.android.utils.Extensions.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Represents dropbox file.
 *
 * @author Vlad Namashko
 */
public class DropboxFile implements FileProxy<DropboxAPI.Entry> {

    private String mName;
    private String mParentPath;
    private DropboxAPI.Entry mEntry;

    private long mSize;
    private long mModified;

    private static final SimpleDateFormat sSimpleDateFormat =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");

    public DropboxFile(DropboxAPI.Entry entry) {
        mEntry = entry;
        mName = entry.fileName();
        mSize = CustomFormatter.parseSize(entry.size);

        if (!entry.isDir) {
            try {
                mModified = sSimpleDateFormat.parse(entry.clientMtime).getTime();
            } catch (Exception e) {
                mModified = 0;
            }
        }

        mParentPath = entry.path.substring(0, entry.path.lastIndexOf("/") + 1);
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
        return mEntry.isDir;
    }

    @Override
    public long getSize() {
        return mSize;
    }

    @Override
    public long lastModifiedDate() {
        return mModified;
    }

    @Override
    public List<DropboxAPI.Entry> getChildren() {
        return mEntry.contents;
    }

    @Override
    public String getFullPath() {
        return mEntry.path;
    }

    @Override
    public String getFullPathRaw() {
        return mEntry.path;
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
        return mEntry.path.equals("/");
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
        return mEntry.mimeType;
    }
}
