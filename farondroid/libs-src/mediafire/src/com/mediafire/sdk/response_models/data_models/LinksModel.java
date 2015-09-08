package com.mediafire.sdk.response_models.data_models;

/**
 * Created by christophernajar on 9/3/15.
 */
public class LinksModel {
    private String view;
    private String edit;
    private String normal_download;

    public String getViewLink() {
        return view;
    }

    public String getEditLink() {
        return edit;
    }

    public String getNormalDownloadLink() {
        return normal_download;
    }
}
