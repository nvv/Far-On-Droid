package com.mediafire.sdk.util;

import com.google.gson.*;
import com.mediafire.sdk.MFApiException;
import com.mediafire.sdk.MFException;
import com.mediafire.sdk.api.responses.ApiResponse;
import com.mediafire.sdk.requests.HttpApiResponse;

/**
 * Created by Chris on 5/15/2015.
 */
public class ResponseUtil {
    public static void validateHttpResponse(HttpApiResponse httpResponse) throws MFException {
        if (httpResponse == null) {
            throw new MFException("HttpApiResponse was null");
        }

        if (httpResponse.getBytes() == null || httpResponse.getBytes().length == 0) {
            throw new MFException("Server gave back a null response");
        }

        if (httpResponse.getHeaderFields() == null || httpResponse.getHeaderFields().isEmpty()) {
            throw new MFException("Server gave back null response headers");
        }

        if (httpResponse.getStatus() < 100) {
            throw new MFException("Server gave back invalid response status: " + httpResponse.getStatus());
        }
    }

    public static void validateConversionHttpResponse(HttpApiResponse httpResponse) throws MFException {
        if (httpResponse == null) {
            throw new MFException("HttpApiResponse was null");
        }

        if (httpResponse.getHeaderFields() == null || httpResponse.getHeaderFields().isEmpty()) {
            throw new MFException("Server gave back null response headers");
        }

        if (httpResponse.getStatus() < 100) {
            throw new MFException("Server gave back invalid response status: " + httpResponse.getStatus());
        }
    }

    public static <T extends ApiResponse> T makeApiResponseFromHttpResponse(HttpApiResponse httpResponse, Class<T> classOfT) throws MFException, MFApiException {
        if (httpResponse == null) {
            throw new MFException("HttpApiResponse was null");
        }

        if (httpResponse.getBytes() == null || httpResponse.getBytes().length == 0) {
            throw new MFException("HttpApiResponse.getBytes() was null or empty, nothing to parse");
        }

        try {
            byte[] responseBytes = httpResponse.getBytes();
            String responseString = new String(responseBytes);
            T apiResponse = new Gson().fromJson(getResponseStringForGson(responseString), classOfT);

            if (apiResponse.hasError()) {
                throw new MFApiException(apiResponse.getError(), apiResponse.getMessage());
            }

            return apiResponse;
        } catch (JsonSyntaxException e) {
            throw new MFException("The json was malformed and could not be read", e);
        }
    }

    private static String getResponseStringForGson(String response) {
        if (response == null || response.isEmpty()) {
            return null;
        }

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(response);
        if (element.isJsonObject()) {
            JsonObject jsonResponse = element.getAsJsonObject().get("response").getAsJsonObject();
            return jsonResponse.toString();
        } else {
            return null;
        }
    }
}
