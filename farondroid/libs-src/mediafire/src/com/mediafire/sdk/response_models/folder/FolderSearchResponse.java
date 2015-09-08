package com.mediafire.sdk.response_models.folder;

import com.mediafire.sdk.response_models.ApiResponse;
import com.mediafire.sdk.response_models.data_models.SearchResultModel;

public class FolderSearchResponse extends ApiResponse {

    private int results_count;

    private SearchResultModel[] results;

    public int getResultsCount() {
        return results_count;
    }

    public SearchResultModel[] getResults() {
        return results;
    }

}
