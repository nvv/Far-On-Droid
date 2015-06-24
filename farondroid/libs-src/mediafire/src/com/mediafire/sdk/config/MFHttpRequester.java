package com.mediafire.sdk.config;

import com.mediafire.sdk.MFException;
import com.mediafire.sdk.log.ApiTransaction;
import com.mediafire.sdk.log.MFLogStore;
import com.mediafire.sdk.requests.GetRequest;
import com.mediafire.sdk.requests.HttpApiResponse;
import com.mediafire.sdk.requests.PostRequest;

public interface MFHttpRequester {
    /**
     * makes a POST request
     * @param postRequest  the PostRequest to make
     * @return an HttpApiResponse
     * @throws com.mediafire.sdk.MFException if an exception is thrown. (e.g. SocketTimeoutException, IOException)
     */
    public HttpApiResponse doApiRequest(PostRequest postRequest) throws MFException;

    /**
     * makes a GET request
     * @param getRequest the GetRequest to make
     * @return an HttpApiResponse
     * @throws com.mediafire.sdk.MFException if an exception is thrown. (e.g. SocketTimeoutException, IOException)
     */
    public HttpApiResponse doApiRequest(GetRequest getRequest) throws MFException;

    /**
     * sets the store for api transactions
     * @param store an MFLogStore of ApiTransaction
     */
    public void setApiTransactionStore(MFLogStore<ApiTransaction> store);
}
