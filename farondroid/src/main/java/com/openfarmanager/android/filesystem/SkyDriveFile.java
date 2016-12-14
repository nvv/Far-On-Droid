package com.openfarmanager.android.filesystem;

import com.openfarmanager.android.App;
import com.openfarmanager.android.core.archive.MimeTypes;
import com.openfarmanager.android.core.network.skydrive.JsonKeys;
import com.openfarmanager.android.model.Bookmark;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;

/**
 * Represents SkyDrive file.
 *
 * @author Vlad Namashko
 */
public class SkyDriveFile implements FileProxy {

    private JSONObject mJsonData;

    private long mModified;
    private String mFullPath;

    private static final SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.US);

    public SkyDriveFile(JSONObject jsonData, String parentPath) {
        mJsonData = jsonData;

        if (!parentPath.endsWith("/")) {
            parentPath += "/";
        }
        mFullPath = parentPath + getName();

        if (!isDirectory()) {
            try {
                mModified = sSimpleDateFormat.parse(tryGet(JsonKeys.UPDATED_TIME, "")).getTime();
            } catch (Exception e) {
                mModified = 0;
            }
        }
    }

    @Override
    public String getId() {
        return tryGet(JsonKeys.ID, "");
    }

    @Override
    public String getName() {
        return tryGet(JsonKeys.NAME, "");
    }

    @Override
    public boolean isDirectory() {
        String type = tryGet(JsonKeys.TYPE, "");
        return JsonKeys.FOLDER.equals(type) || !mJsonData.isNull(JsonKeys.COUNT);
    }

    @Override
    public long getSize() {
        return tryGet(JsonKeys.SIZE, 0);
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
        return getId();
    }

    @Override
    public String getFullPathRaw() {
        return mFullPath;
    }

    @Override
    public String getParentPath() {
        return tryGet(JsonKeys.PARENT_ID, "");
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
        String type = tryGet(JsonKeys.TYPE, "");

        switch (type) {
            case "photo":
                return MimeTypes.MIME_IMAGE;
            case "video":
                return MimeTypes.MIME_VIDEO;
            case "audio":
                return MimeTypes.MIME_AUDIO;
        }

        return type;
    }

    public String getSource() {
        return tryGet(JsonKeys.SOURCE, "");
    }

    private String tryGet(String name, String defaultValue) {
        try {
            return mJsonData.getString(name);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private long tryGet(String name, long defaultValue) {
        try {
            return mJsonData.getLong(name);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
