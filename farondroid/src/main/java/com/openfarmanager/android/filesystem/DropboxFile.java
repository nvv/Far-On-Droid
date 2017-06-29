package com.openfarmanager.android.filesystem;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.openfarmanager.android.core.archive.MimeTypes;
import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.model.MimeTypeEnum;
import com.openfarmanager.android.utils.CustomFormatter;
import com.openfarmanager.android.utils.FileUtilsExt;

import org.apache.commons.io.FilenameUtils;
import org.apache.james.mime4j.util.MimeUtil;

import static com.openfarmanager.android.utils.Extensions.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Represents dropbox file.
 *
 * @author Vlad Namashko
 */
public class DropboxFile implements FileProxy {

    private String mName;
    private String mParentPath;

    private String mMimeType;

    private String mPath;
    private boolean mIsFolder;
    private long mSize;
    private long mModified;

    public DropboxFile(Metadata entry) {
        mName = entry.getName();

        mPath = entry.getPathLower();
        if (entry instanceof FileMetadata) {
            FileMetadata fileMetadata = (FileMetadata) entry;

            mSize = fileMetadata.getSize();

            Date serverModified = fileMetadata.getServerModified();
            Date clientModified = fileMetadata.getClientModified();

            Date modified = null;
            if (clientModified != null && serverModified != null) {
                modified = clientModified.after(serverModified) ? clientModified : serverModified;
            } else if (clientModified != null) {
                modified = clientModified;
            } else if (serverModified != null) {
                modified = serverModified;
            }

            mModified = modified != null ? modified.getTime() : new Date().getTime();
            mMimeType = MimeTypes.lookupMimeType(FilenameUtils.getExtension(mName));
        } else {
            mSize = 0;
            mIsFolder = true;
        }

        mParentPath = entry.getPathLower().substring(0, entry.getPathLower().lastIndexOf("/") + 1);
    }

    public DropboxFile(String path) {
        path = FileUtilsExt.removeLastSeparator(path);
        mName = FileUtilsExt.getFileName(path);

        mPath = path;
        mSize = 0;
        mModified = System.currentTimeMillis();
        mParentPath = path.substring(0, path.lastIndexOf("/") + 1);
    }

    @Override
    public String getId() {
        return getFullPath();
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public boolean isDirectory() {
        return mIsFolder;
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
    public List getChildren() {
        return new ArrayList();
    }

    @Override
    public String getFullPath() {
        return mPath;
    }

    @Override
    public String getFullPathRaw() {
        return mPath;
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
        return mPath.equals("/");
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
        return mMimeType;
    }
}
