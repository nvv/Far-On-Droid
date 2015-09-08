package com.mediafire.sdk.response_models.user;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.UserSettingsModel;

/**
 * Created by jondh on 10/1/14.
 */
public class UserGetSettingsResponse extends ApiResponse {

    public UserSettingsModel settings;

    public UserSettingsModel getSettings() {
        return settings;
    }

}
