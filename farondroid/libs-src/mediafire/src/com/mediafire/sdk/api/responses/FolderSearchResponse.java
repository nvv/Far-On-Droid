package com.mediafire.sdk.api.responses;

import com.mediafire.sdk.api.responses.data_models.SearchResult;

public class FolderSearchResponse extends ApiResponse {

    private int results_count;

    private SearchResult[] results;

    public int getResultsCount() {
        return results_count;
    }

    public SearchResult[] getResults() {
        return results;
    }

}
