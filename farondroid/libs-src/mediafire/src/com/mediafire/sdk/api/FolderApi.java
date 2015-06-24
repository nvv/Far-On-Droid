package com.mediafire.sdk.api;

import com.mediafire.sdk.MFApiException;
import com.mediafire.sdk.MFException;
import com.mediafire.sdk.MFSessionNotStartedException;
import com.mediafire.sdk.MediaFire;
import com.mediafire.sdk.api.responses.*;
import com.mediafire.sdk.requests.ApiPostRequest;

import java.util.LinkedHashMap;

/**
 * http://www.mediafire.com/developers/core_api/1.3/getting_started/
 * @see <a href="http://www.mediafire.com/developers/core_api/1.3/getting_started/">MediaFire Developer Portal</a>
 */
public class FolderApi {

    private FolderApi() {
        // no instantiation, utility class
    }

    /**
     * Copy a folder and its content to another folder. If the folder is not owned by the session user,
     * it will be added as a linked folder.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T copy(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/folder/copy.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Creates a folder.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T create(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/folder/create.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Moves one folder to another folder.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T move(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/folder/move.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Delete a user's folder. If called, the folder is not deleted permanently but,
     * rather, the folder is moved to the trash can.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T delete(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/folder/delete.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Permanently delete a user's folder.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T purge(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/folder/purge.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Update a folder's information.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T update(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/folder/update.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Returns a list of a folder's details.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T getInfo(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/folder/get_info.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Returns either a list of folders or a list of files.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T getContent(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/folder/get_content.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Searches the the content of a given folder. If folder_key is not passed, then the search will be performed
     * on the root folder ("myfiles"). In this case, the session token will be required. To search the root folder on
     * devices other than the cloud, pass the device_id. If device_id is '-1', then a global search on all
     * devices will be performed.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T search(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/folder/search.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }
}
