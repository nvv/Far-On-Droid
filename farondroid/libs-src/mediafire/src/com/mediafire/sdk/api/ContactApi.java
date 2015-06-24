package com.mediafire.sdk.api;

import com.mediafire.sdk.MFApiException;
import com.mediafire.sdk.MFException;
import com.mediafire.sdk.MFSessionNotStartedException;
import com.mediafire.sdk.MediaFire;
import com.mediafire.sdk.api.responses.*;
import com.mediafire.sdk.requests.ApiPostRequest;

import java.util.LinkedHashMap;

/**
 *
 * @see <a href="http://www.mediafire.com/developers/core_api/1.3/getting_started/">MediaFire Developer Portal</a>
 */
public class ContactApi {


    private ContactApi() {
        // no instantiation, utility class
    }

    /**
     * Adds a new contact, updates an existing contact, or imports/syncs a third-party contact to the current session user's contact list.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T add(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/contact/add.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Delete a contact from the current session user's contact list.
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
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/contact/delete.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Returns the contact list (contactkey, display name, first name, last name, avatar, email, phone, means_of_contact,
     * birthdate, location, gender, website, created date, & contact type), revision, and count of the current session user.
     * If method = "autocomplete", the contact list is returned as a trie (radix tree) structure.
     * It is faster to traverse the trie branches using contact names and emails to find contacts.
     * The trie is saved can be returned "raw" or in a "succinct" format.
     * The response will contain the trie (base-64 encoded), the directory (base-64 encoded), and the number of nodes.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T fetch(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/contact/fetch.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Returns the URL of a given contact's avatar image.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T getAvatar(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/contact/get_avatar.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Get contact sources in descending order of precedence.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T getSources(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/contact/get_sources.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Save a copy of a given contact's remote avatar image for use in the operating user's list.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T setAvatar(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/contact/set_avatar.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }

    /**
     * Fetch a summary of contacts by type, including direct vs. indirect.
     *
     * @param mediaFire an instance of MediaFire which has a session in progress
     * @param requestParams a LinkedHashMap of required and optional parameters
     * @param apiVersion version of the api to call e.g. 1.0, 1.1, 1.2
     * @param classOfT the .class file passed which will be used to parse the api JSON response using Gson (must extend ApiResponse)
     * @return an instance of {@param classOfT}
     * @throws com.mediafire.sdk.MFException if an exception occurred
     * @throws MFApiException, MFSessionNotStartedException if there was an api error
     */
    public static <T extends ApiResponse> T summary(MediaFire mediaFire, LinkedHashMap<String, Object> requestParams, String apiVersion, Class<T> classOfT) throws MFException, MFApiException, MFSessionNotStartedException {
        ApiPostRequest apiPostRequest = new ApiPostRequest("/api/" + apiVersion + "/contact/summary.php", requestParams);
        return mediaFire.doApiRequest(apiPostRequest, classOfT);
    }
}
