package com.mediafire.sdk.api;

import com.mediafire.sdk.MFApiException;
import com.mediafire.sdk.MFException;
import com.mediafire.sdk.MFSessionNotStartedException;
import com.mediafire.sdk.MediaFire;
import com.mediafire.sdk.api.responses.*;
import com.mediafire.sdk.requests.ApiPostRequest;
import com.mediafire.sdk.requests.UploadPostRequest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * http://www.mediafire.com/developers/core_api/1.3/getting_started/
 * @see <a href="http://www.mediafire.com/developers/core_api/1.3/getting_started/">MediaFire Developer Portal</a>
 */
public class UploadApi {

    private UploadApi() {
        // no instantiation, utility class
    }

    /**
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T check(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/upload/check.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     *  Checks if a duplicate filename exists in the destination folder and verifies folder permissions for non-owner uploads.
     *  When a hash is supplied, hash_exists is returned to indicate whether an instant upload is possible.
     *  Several flags are returned, which can be "yes" or "no": file_exists (with the same name and location),
     *  different_hash (if file_exists), hash_exists (somewhere in the cloud), in_account (if hash_exists),
     *  and in_folder (if hash_exists). If a path is supplied, a folder_key will also be returned to be used
     *  for a subsequent upload. If resumable is supplied as "yes", a resumable upload will be initiated,
     *  and resumable_upload will be returned containing the relevant data.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T instant(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/upload/instant.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }


    /**
     * Check for the status of a current Upload. This can be called after using any of the upload family of APIs which
     * return an upload key. Use the key returned (response.doupload.key) to request the status of the current upload.
     * Keep calling this API every few seconds until you get the status value 99 which means that the upload is complete.
     * The quickkey of the file and other related information is also returned when the upload is complete.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T pollUpload(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/upload/poll_upload.php", requestParams, false);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Adds a new web upload and returns the Upload Key on success.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T addWebUpload(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/upload/add_web_upload.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Returns a list of web uploads currently in progress or all web uploads.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T getWebUploads(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/upload/get_web_uploads.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * @see <a href="http://www.mediafire.com/developers/core_api/1.3/upload/#upload_top">MediaFire UploadConcepts</a>
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param headerParameters a Map of required and optional headers
     * @param payload the byte[] payload to upload
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T resumable(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, Map<String, Object> headerParameters, byte[] payload, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        UploadPostRequest uploadPostRequest = new UploadPostRequest("/api/" + apiVersion + "/upload/resumable.php", requestParams, headerParameters, payload);
        return mediaFire.doUploadRequest(uploadPostRequest, classOfT);
    }

    /**
     * Update an existing file in the user's account with another file. This API returns the upload key when successful.
     * You will have to pass this key to upload/poll_upload.php to check the final result of the update upload.
     * Please refer to the documentation about the API upload/poll_upload for more details.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param headerParameters a Map of required and optional headers
     * @param payload the byte[] payload to upload
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T update(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, Map<String, Object> headerParameters, byte[] payload, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        UploadPostRequest uploadPostRequest = new UploadPostRequest("/api/" + apiVersion + "/upload/update.php", requestParams, headerParameters, payload);
        return mediaFire.doUploadRequest(uploadPostRequest, classOfT);
    }

    /**
     * Upload a new file through POST to the user's account. You can either use the session token to authenticate the user,
     * or pass the FileDrop folder key. This API returns the upload key when successful.
     * You will have to pass this key to upload/poll_upload to get the quickkey.
     * Please refer to the documentation about the API upload/poll_upload for more details.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param headerParameters a Map of required and optional headers
     * @param payload the byte[] payload to upload
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T simple(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, Map<String, Object> headerParameters, byte[] payload, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        UploadPostRequest uploadPostRequest = new UploadPostRequest("/api/" + apiVersion + "/upload/simple.php", requestParams, headerParameters, payload);
        return mediaFire.doUploadRequest(uploadPostRequest, classOfT);
    }
}
