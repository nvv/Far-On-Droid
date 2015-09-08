package com.mediafire.sdk;

import com.google.gson.*;
import com.mediafire.sdk.response_models.MediaFireApiResponse;
import com.mediafire.sdk.util.TextUtils;

public class MFApiResponseParser implements MediaFireApiResponseParser {

    public MFApiResponseParser() {

    }

    @Override
    public <T extends MediaFireApiResponse> T parseResponse(MediaFireHttpResponse response, Class<T> classOfT) throws MediaFireException {
        if (response == null) {
            throw new MediaFireException("MediaFireHttpResponse was null while trying to parse an ApiResponse");
        }
        byte[] responseBytes = response.getBody();

        if (responseBytes == null || responseBytes.length == 0) {
            throw new MediaFireException("MediaFireHttpResponse was null while trying to parse an ApiResponse");
        }

        try {
            String byteResponseAsString = new String(responseBytes);
            String responseString = getResponseString(byteResponseAsString);
            if (TextUtils.isEmpty(byteResponseAsString)) {
                throw new MediaFireException("response string was null or empty and could not be parsed");
            }
            return new Gson().fromJson(responseString, classOfT);
        } catch (JsonSyntaxException e) {
            throw new MediaFireException("Malformed Json response", e);
        }
    }

    @Override
    public String getResponseFormat() {
        return "json";
    }

    private String getResponseString(String response) {
        if (TextUtils.isEmpty(response)) {
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
