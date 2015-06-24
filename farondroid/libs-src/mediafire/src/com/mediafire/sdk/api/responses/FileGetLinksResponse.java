package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.Link;

public class FileGetLinksResponse extends ApiResponse {

    private long direct_download_free_bandwidth;
    private int one_time_download_request_count;

    private Link[] links;

    public long getDirectDownloadFreeBandwidth() {
        return this.direct_download_free_bandwidth;
    }

    public int getOneTimeDownloadRequestCount() {
        return this.one_time_download_request_count;
    }

    public Link[] getLinks() {
        return links;
    }

}
