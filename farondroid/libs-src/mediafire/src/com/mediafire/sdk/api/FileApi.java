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
public class FileApi {

    private FileApi() {
        // no instantiation, utility class
    }

    /**
     * Returns a list of the file's details. This call will return the quickkey, filename, creation date,
     * description, status, size, flag ...etc.
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
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/file/get_info.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Deletes one or more session user files by setting the files' delete_date property and moving the files to
     * the trash can. Once a file has been moved to the Trash Can it is still accessible through device/get_trash.
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
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/file/delete.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Copies a file from one account or location another account or location. Any file can be copied whether it
     * belongs to the session user or another user. However, the target folder must be owned by the session caller.
     * Private files not owned by the session caller cannot be copied.
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
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/file/copy.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Returns a list of all file versions.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T getVersion(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/file/get_version.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Move a file, or list of files, to a different location.
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
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/file/move.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Update a file's information.
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
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/file/update.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Returns the view link, normal download link, and, if possible, the direct download link of a file.
     * If the direct download link is not returned, an error message is returned explaining the reason.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T getLinks(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/file/get_links.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }
}
