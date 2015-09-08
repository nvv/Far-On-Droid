package com.mediafire.sdk.response_models.data_models;

/**
* Created by Chris on 5/14/2015.
*/
public class FileInfosModel extends FileInfoModel {
    private String view;
    private String edit;
    private LinksModel links;

    public String getView() {
        return view;
    }

    public String getEdit() {
        return edit;
    }

    public LinksModel getLinks() {
        return links;
    }

}
