package com.openfarmanager.android.filesystem;

import com.mediafire.sdk.api.responses.data_models.File;
import com.mediafire.sdk.api.responses.data_models.FileInfo;
import com.mediafire.sdk.api.responses.data_models.Folder;
import com.openfarmanager.android.App;
import com.openfarmanager.android.model.Bookmark;

import java.util.ArrayList;
import java.util.List;

import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;

/**
 * @author Vlad Namashko
 */
public class MediaFireFile implements FileProxy {

    private String mId;
    private String mName;
    private boolean mIsDirectory;
    private long mSize;
    private String mFullPath;
    private String mParentPath;
    private String mMimeType;
    private String mFileType;

    public MediaFireFile(Folder folder, String parentPath, String parentPathRaw) {
        mIsDirectory = true;
        mId = folder.getFolderkey();
        mName = folder.getFolderName();
        mSize = folder.getSize();
        mParentPath = parentPath;
        if (parentPathRaw != null) {
            mFullPath = parentPathRaw.endsWith("/") ? parentPathRaw + getName() : parentPathRaw + "/" + getName();
        }
    }

    public MediaFireFile(File file, String parentPath, String parentPathRaw) {
        mIsDirectory = false;
        mId = file.getQuickKey();
        mName = file.getFilename();
        mSize = file.getSize();
        mParentPath = parentPath;
        mMimeType = file.getMimeType();
        mFileType = file.getFileType();
        if (parentPathRaw != null) {
            mFullPath = parentPathRaw.endsWith("/") ? parentPathRaw + getName() : parentPathRaw + "/" + getName();
        }
    }

    public MediaFireFile(FileInfo info) {
        mIsDirectory = false;
        mId = info.getQuickKey();
        mName = info.getFileName();
        mSize = info.getSize();
        mParentPath = info.getParentFolderKey();
        mMimeType = info.getMimeType();
        mFileType = info.getFileType();
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
        return mIsDirectory;
    }

    @Override
    public long getSize() {
        return mSize;
    }

    @Override
    public long lastModifiedDate() {
        return 0;
    }

    @Override
    public List getChildren() {
        return new ArrayList();
    }

    @Override
    public String getFullPath() {
        return getId();
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
        return false;
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
