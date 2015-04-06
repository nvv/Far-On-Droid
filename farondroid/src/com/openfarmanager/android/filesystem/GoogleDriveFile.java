package com.openfarmanager.android.filesystem;

import com.openfarmanager.android.App;
import com.openfarmanager.android.core.archive.MimeTypes;
import com.openfarmanager.android.googledrive.model.File;
import com.openfarmanager.android.model.Bookmark;

import java.util.HashMap;
import java.util.List;

import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;

/**
 * author: Vlad Namashko
 */
public class GoogleDriveFile implements FileProxy {

    private File mFile;
    private String mFullPath;

    public GoogleDriveFile(File file, String parentPath) {
        mFile = file;

        String cachedValue = App.sInstance.getGoogleDriveApi().getFoldersAliases().get(parentPath);
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
        return mFile.getId();
    }

    @Override
    public String getName() {
        return mFile.getName();
    }

    @Override
    public boolean isDirectory() {
        return mFile.isDirectory();
    }

    @Override
    public long getSize() {
        return mFile.getSize();
    }

    @Override
    public long lastModifiedDate() {
        return mFile.getLastModifiedDate();
    }

    @Override
    public List getChildren() {
        return null;
    }

    @Override
    public String getFullPath() {
        App.sInstance.getGoogleDriveApi().getFoldersAliases().put(getId(), mFullPath);
        return mFile.getId();
    }

    @Override
    public String getFullPathRaw() {
        return mFullPath;
    }

    @Override
    public String getParentPath() {
        return mFile.getParentPath();
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
        return mFile.isVirtual();
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
        String type = mFile.getMimeType();

        if (type.startsWith("image")) {
            return MimeTypes.MIME_IMAGE;
        } else if (type.startsWith("video")) {
            return MimeTypes.MIME_VIDEO;
        } else if (type.startsWith("audio")) {
            return MimeTypes.MIME_AUDIO;
        }

        return type;
    }

    public String getDownloadLink() {
        return mFile.getDownloadLink();
    }

    public HashMap<String, String> getExportLinks() {
        return mFile.getExportLinks();
    }

    public boolean hasOpenWithLink() {
        return getOpenWithLink() != null;
    }

    public String getOpenWithLink() {
        return mFile.getOpenWithLink();
    }
}
