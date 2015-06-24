package com.mediafire.sdk.api.responses.data_models;

/**
* Created by Chris on 5/14/2015.
*/
public class FileInfos extends FileInfo {
    private String view;
    private String edit;
    private Links links;

    public String getView() {
        return view;
    }

    public String getEdit() {
        return edit;
    }

    public Links getLinks() {
        return links;
    }

    private class Links {
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
}
