package com.mediafire.sdk.api.responses.data_models;

/**
* Created by Chris on 5/14/2015.
*/
public class MyFilesRevision {
    private String revision;
    private String epoch;

    public String getRevision() {
        if (this.revision == null) {
            this.revision = "";
        }
        return this.revision;
    }

    public long getEpoch() {
        if (this.epoch == null) {
            this.epoch = "0";
        }
        return Long.valueOf(this.epoch);
    }
}
