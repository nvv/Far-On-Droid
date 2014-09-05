/**
 * Bitcasa Client Android SDK
 * Copyright (C) 2013 Bitcasa, Inc.
 * 215 Castro Street, 2nd Floor
 * Mountain View, CA 94041
 *
 * This file contains an SDK in Java for accessing the Bitcasa infinite drive in Android platform.
 *
 * For support, please send email to support@bitcasa.com.
 */
package com.bitcasa.client.HTTP;

public class BitcasaRESTConstants {
	
	public static final String SERVER_URL = "https://developer.api.bitcasa.com";
	public static final String REDIRECT_URI = "https://localhost/app/";
	public static final String VERSION = "/v1";
	
	public static final String FORESLASH = "/";
	
	public static final String UTF_8_ENCODING = "UTF-8";	
	
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_CONTENT_TYPE_APP_URLENCODED = "application/x-www-form-urlencoded;charset=UTF-8";
	public static final String HEADER_ACCEPT_CHARSET = "Accept-Charset";
	public static final String HEADER_XAUTH = "XAuth";
	public static final String HEADER_RANGE = "Range";
	public static final String HEADER_CONNECTION = "Connection";
	public static final String HEADER_CONNECTION_KEEP_ALIVE = "Keep-Alive";
	public static final String HEADER_ENCTYPE = "ENCTYPE";
	public static final String HEADER_ENCTYPE_MULTIPART = "multipart/form-data";
	public static final String HEADER_CONTENT_TYPE_MULTIPART_BOUNDARY = "multipart/form-data;boundary=";
	public static final String HEADER_FILE = "file";
	
	public static final String REQUEST_METHOD_GET = "GET";
	public static final String REQUEST_METHOD_POST = "POST";
	public static final String REQUEST_METHOD_PUT = "PUT";
	public static final String REQUEST_METHOD_DELETE = "DELETE";
	
	public static final String METHOD_AUTHENTICATE = "/authenticate";
	public static final String METHOD_OAUTH2 = "/oauth2";
	public static final String METHOD_ACCESS_TOKEN = "/access_token";
	public static final String METHOD_AUTHORIZE = "/authorize";
	public static final String METHOD_TOKEN = "/token";
	public static final String METHOD_FOLDERS = "/folders";
	public static final String METHOD_FILES = "/files";
	public static final String METHOD_USER = "/user";
	public static final String METHOD_PROFILE = "/profile";
	
	public static final String PARAM_CLIENT_ID = "client_id";
	public static final String PARAM_REDIRECT = "redirect";
	public static final String PARAM_USER = "user";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_SECRET = "secret";
	public static final String PARAM_CODE = "code";
	public static final String PARAM_RESPONSE_TYPE = "response_type";
	public static final String PARAM_REDIRECT_URI = "redirect_uri";
	public static final String PARAM_GRANT_TYPE = "grant_type";
	public static final String PARAM_PATH = "path";
	public static final String PARAM_FOLDER_NAME = "folder_name";	
	public static final String PARAM_ACCESS_TOKEN = "access_token";
	public static final String PARAM_DEPTH = "depth";
	public static final String PARAM_LATEST = "latest";
	public static final String PARAM_CATEGORY = "category";
	public static final String PARAM_ID = "id";
	public static final String PARAM_INDIRECT = "indirect";
	public static final String PARAM_FILENAME = "filename";
	public static final String PARAM_EXISTS = "exists";
	public static final String PARAM_OPERATION = "operation";
	
	public static final String BODY_FOLDERNAME = "folder_name";
	public static final String BODY_FILE = "file";
	public static final String BODY_FROM = "from";
	public static final String BODY_TO = "to";
	public static final String BODY_EXISTS = "exists";
	public static final String BODY_FILENAME = "filename";
	public static final String BODY_PATH = "path";
	
	public static final String OPERATION_COPY = "copy";
	public static final String OPERATION_MOVE = "move";
	public static final String OPERATION_RENAME = "rename";
	
	public static final String EXISTS_FAIL = "fail";
	public static final String EXISTS_OVERWRITE = "overwrite";
	public static final String EXISTS_RENAME = "rename";
	
	// update progress interval
	public static final long PROGRESS_UPDATE_INTERVAL			= 2000;
	
	// categories
	public static enum Category {
		MUSIC_ARTISTS("music_artists"),
		MUSIC_ALBUMS("music_albums"),
		MUSIC_TRACKS("music_tracks"),
		PHOTO_ALBUMS("photo_albums"),
		PHOTOS("photos"),
		DOCUMENTS("documents"),
		VIDEOS("videos"),
		EVERYTHING("everything");
		
		private final String mCategory;
		
		private Category(String category) {
			mCategory = category;
		}
		
		public String getCategory() {
			return mCategory;
		}
		
		public static Category getEnum(String s) {
			for (Category category : Category.values()) {
				if (category.toString().equals(s)) {
					return category;
				}
			}
			return null;
		}
	}
	
	public static enum Depth {
		INFINITE,
		CURRENT_CHILDREN
	}
	
	public static enum FileType {
		BITCASA_TYPE_FILE,
		BITCASA_TYPE_FOLDER
	}
	
	public enum FileOperation {
        DELETE, COPY, MOVE, RENAME, ADDFOLDER;
    }
	
	public enum CollisionResolutions {
		FAIL, RENAME, OVERWRITE;
	}
	
}
