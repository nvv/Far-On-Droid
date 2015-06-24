package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.UserSettings;

/**
 * Created by jondh on 10/1/14.
 */
public class UserGetSettingsResponse extends ApiResponse {

    public UserSettings settings;

    public UserSettings getSettings() {
        return settings;
    }

}
