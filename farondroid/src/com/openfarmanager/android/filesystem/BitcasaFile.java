package com.openfarmanager.android.filesystem;

import com.bitcasa.client.HTTP.BitcasaRESTConstants;
import com.bitcasa.client.datamodel.FileMetaData;
import com.openfarmanager.android.App;
import com.openfarmanager.android.model.Bookmark;

import java.util.List;

import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;

/**
 * Represents Bitcasa file.
 *
 * @author Vlad Namashko
 */
public class BitcasaFile implements FileProxy {

    private FileMetaData mMetaData;
    private String mParentPath;
    private String mFullPath;

    public BitcasaFile(FileMetaData data, String parentPath) {
        mMetaData = data;

        mParentPath = parentPath;

        String cachedValue = App.sInstance.getBitcasaApi().getFoldersAliases().get(parentPath);
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
        return mMetaData.path;
    }

    @Override
    public String getName() {
        return mMetaData.name;
    }

    @Override
    public boolean isDirectory() {
        return mMetaData.type == BitcasaRESTConstants.FileType.BITCASA_TYPE_FOLDER;
    }

    @Override
    public long getSize() {
        return mMetaData.size;
    }

    @Override
    public long lastModifiedDate() {
        return mMetaData.mtime;
    }

    @Override
    public List getChildren() {
        return null;
    }

    @Override
    public String getFullPath() {
        App.sInstance.getBitcasaApi().getFoldersAliases().put(getId(), mFullPath);
        return getId();
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
