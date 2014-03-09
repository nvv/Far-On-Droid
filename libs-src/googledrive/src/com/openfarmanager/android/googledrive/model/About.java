package com.openfarmanager.android.googledrive.model;

import com.openfarmanager.android.googledrive.api.Fields;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * author: Vlad Namashko
 */
public class About {

    private String mPermissionId;
    private String mDisplayName;

    public About(String jsonData) throws JSONException {
        JSONObject object = new JSONObject(jsonData);
        JSONObject user = object.getJSONObject(Fields.USER);
        mPermissionId = user.getString(Fields.PERMISSION_ID);
        mDisplayName = user.getString(Fields.DISPLAY_NAME);
    }

    public String getPermissionId() {
        return mPermissionId;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

}
