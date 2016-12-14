package com.openfarmanager.android.model;

import android.content.res.Resources;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;

/**
 * @author Vlad Namashko
 */
public enum MimeTypeEnum {
    Text, Image, Video, Audio;

    public static String getMimeLabel(MimeTypeEnum mimeType) {
        Resources resources = App.sInstance.getResources();
        switch (mimeType) {
            case Video: return resources.getString(R.string.mime_type_video);
            case Audio: return resources.getString(R.string.mime_type_audio);
            case Image: return resources.getString(R.string.mime_type_image);
            case Text: default: return resources.getString(R.string.mime_type_text);
        }
    }

    public static String getMime(MimeTypeEnum mimeType) {
        switch (mimeType) {
            case Video: return "video/*";
            case Audio: return "audio/*";
            case Image: return "image/*";
            case Text: default: return "text/*";
        }
    }
}
