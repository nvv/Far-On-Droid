package com.openfarmanager.android.googledrive.model;

import com.openfarmanager.android.googledrive.api.Fields;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * author: Vlad Namashko
 */
public class File {

    private static SimpleDateFormat sFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private static Map<String, String> sExportMimes;

    static {
        sExportMimes = new HashMap<String, String>();

        sExportMimes.put("text/html", "HTML");
        sExportMimes.put("text/plain", "Plain text");
        sExportMimes.put("application/rtf", "Rich text");
        sExportMimes.put("application/vnd.oasis.opendocument.text", "Open Office doc");
        sExportMimes.put("application/pdf", "PDF");
        sExportMimes.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "MS Word document");
        sExportMimes.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "MS Excel");
        sExportMimes.put("application/x-vnd.oasis.opendocument.spreadsheet", "Open Office sheet");
        sExportMimes.put("application/pdf", "PDF");
        sExportMimes.put("image/jpeg", "JPEG");
        sExportMimes.put("image/png", "PNG");
        sExportMimes.put("image/svg+xml", "SVG");
        sExportMimes.put("application/pdf", "PDF");
        sExportMimes.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", "MS PowerPoint");
        sExportMimes.put("application/pdf", "Open Office, PDF");
    }

    protected String mId;
    protected String mName;
    protected String mMimeType;
    protected boolean mIsDirectory;
    protected long mSize;
    protected long mLastModifiedDate;
    protected String mFullPath;
    protected String mParentPath;
    protected String mDownloadUr;
    protected HashMap<String, String> mExportLinks;

    public File(String json) throws JSONException, ParseException {
        this(new JSONObject(json));
    }

    public File(JSONObject json) throws JSONException, ParseException {
        mId = json.getString("id");
        mName = json.getString("title");
        mMimeType = json.getString("mimeType");
        mIsDirectory = mMimeType.equals(Fields.FOLDER_MIME_TYPE);
        if (!mIsDirectory && json.has("fileSize")) {
            mSize = json.getLong("fileSize");
        }
        mLastModifiedDate = sFormatter.parse(json.getString("modifiedDate")).getTime();
        mParentPath = ((JSONObject) json.getJSONArray("parents").get(0)).getString("id");

        if (!isDirectory()) {
            if (json.has("downloadUrl")) {
                mDownloadUr = json.getString("downloadUrl");
            } else if (json.has("exportLinks")) {
                JSONObject exportLinks = json.getJSONObject("exportLinks");
                Iterator<String> keys = exportLinks.keys();
                mExportLinks = new HashMap<String, String>();
                while (keys.hasNext()) {
                    String key = keys.next();
                    mExportLinks.put(key, exportLinks.getString(key));
                }
            }

        }

    }


    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public long getSize() {
        return mSize;
    }

    public long getLastModifiedDate() {
        return mLastModifiedDate;
    }

    public String getFullPath() {
        return mFullPath;
    }

    public String getParentPath() {
        return mParentPath;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public boolean isDirectory() {
        return mIsDirectory;
    }

    public String getDownloadLink() {
        if (mDownloadUr != null) {
            return mDownloadUr;
        }

        if (mExportLinks != null && mExportLinks.size() > 0) {
            return (String) mExportLinks.values().toArray()[0];
        }

        return "";
    }

    public HashMap<String, String> getExportLinks() {
        return mExportLinks;
    }

    public static String getExportLinkAlias(String key) {
        return sExportMimes.get(key);
    }
}
