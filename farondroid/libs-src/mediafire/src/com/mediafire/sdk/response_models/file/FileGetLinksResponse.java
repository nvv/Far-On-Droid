package com.mediafire.sdk.response_models.file;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.LinkModel;

public class FileGetLinksResponse extends ApiResponse {

    private long direct_download_free_bandwidth;
    private int one_time_download_request_count;

    private LinkModel[] links;

    public long getDirectDownloadFreeBandwidth() {
        return this.direct_download_free_bandwidth;
    }

    public int getOneTimeDownloadRequestCount() {
        return this.one_time_download_request_count;
    }

    public LinkModel[] getLinks() {
        return links;
    }

}
