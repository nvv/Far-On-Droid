package com.openfarmanager.android.filesystem;

import com.mediafire.sdk.api.responses.data_models.File;
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

    public MediaFireFile(Folder folder, String parentPath) {
        mIsDirectory = true;
        mId = folder.getFolderkey();
        mName = folder.getFolderName();
        mSize = folder.getSize();
        mParentPath = parentPath;
        calculateFullPath(parentPath);
    }

    public MediaFireFile(File file, String parentPath) {
        mIsDirectory = false;
        mId = file.getQuickKey();
        mName = file.getFilename();
        mSize = file.getSize();
        mParentPath = parentPath;
        mMimeType = file.getMimeType();
        mFileType = file.getFileType();
        calculateFullPath(parentPath);
    }

    private void calculateFullPath(String parentPath) {
        String cachedValue = App.sInstance.getMediaFireApi().getFoldersAliases().get(parentPath);
        if (!isNullOrEmpty(cachedValue)) {
            parentPath = cachedValue;
            if (!parentPath.endsWith("/")) {
                parentPath += "/";
            }
        }

        mFullPath = parentPath + getName();
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
        App.sInstance.getMediaFireApi().getFoldersAliases().put(getId(), mFullPath);
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
