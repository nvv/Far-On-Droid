package com.mediafire.sdk.requests;

import com.mediafire.sdk.token.ActionToken;

import java.util.HashMap;
import java.util.Map;

/**
 * GetRequest is used to make api requests via GET
 */
public class GetRequest {
    private static final String BASE_URL = "https://www.mediafire.com/conversion_server.php?";
    private final String url;
    private final Map<String, Object> headers = new HashMap<String, Object>();

    public GetRequest(ImageRequest imageRequest, ActionToken imageToken) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(BASE_URL);
        urlBuilder.append(imageRequest.getHash().substring(0, 4));
        urlBuilder.append("&quickkey=").append(imageRequest.getQuickKey());
        urlBuilder.append("&doc_type=i");
        urlBuilder.append("&size_id=").append(imageRequest.getSizeId());

        if (imageRequest.isConversionOnly()) {
            urlBuilder.append("&request_conversion_only=1");
        }

        urlBuilder.append("&session_token=").append(imageToken.getToken());

        url = urlBuilder.toString();
    }

    public GetRequest(DocumentRequest documentRequest, ActionToken imageToken) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(BASE_URL);
        urlBuilder.append(documentRequest.getHash().substring(0, 4));
        urlBuilder.append("&quickkey=").append(documentRequest.getQuickKey());
        urlBuilder.append("&doc_type=d");
        urlBuilder.append("&page=").append(documentRequest.getPage());
        urlBuilder.append("&session_token=").append(imageToken.getToken());

        if (documentRequest.getOptionalParameters() != null) {
            DocumentRequest.OptionalParameters optionalParameters = documentRequest.getOptionalParameters();

            int sizeId = optionalParameters.getSizeId();

            urlBuilder.append("&output=").append(optionalParameters.getOutput());

            if (DocumentRequest.OptionalParameters.OUTPUT_IMG.equals(optionalParameters.getOutput()) && sizeId != -1) {
                urlBuilder.append("&size_id=").append(sizeId);
            }

            if (optionalParameters.isRequestingConversionOnly()) {
                urlBuilder.append("&request_conversion_only=1");
            }

            if (optionalParameters.isRequestingJSONEncodedData()) {
                urlBuilder.append("&metadata=1");
            }
        }
        url = urlBuilder.toString();
    }

    public String getUrl() {
        return url;
    }


    public Map<String, Object> getHeaders() {
        return headers;
    }
}
