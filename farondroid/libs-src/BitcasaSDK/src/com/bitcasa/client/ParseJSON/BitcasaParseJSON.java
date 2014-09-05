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
package com.bitcasa.client.ParseJSON;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.bitcasa.client.HTTP.BitcasaRESTConstants;
import com.bitcasa.client.HTTP.BitcasaRESTConstants.FileType;
import com.bitcasa.client.datamodel.AccountInfo;
import com.bitcasa.client.datamodel.BitcasaError;
import com.bitcasa.client.datamodel.FileMetaData;
import com.bitcasa.client.exception.BitcasaException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;


public class BitcasaParseJSON {
	
	private static final String TAG_ERROR = "error";
	private static final String TAG_RESULT = "result";
	private static final String TAG_DELETED = "deleted";
	//private static final String TAG_AUTH_CODE = "authorization_code";
	private static final String TAG_ACCESS_TOKEN = "access_token";
	private static final String TAG_ITEMS = "items";
	
	private static final String TAG_ERROR_CODE = "code";
	private static final String TAG_ERROR_MESSAGE = "message";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_ALBUM = "album";
	private static final String TAG_NAME = "name";
	private static final String TAG_EXTENSION = "extension";
	private static final String TAG_DUPLICATES = "duplicates";
	private static final String TAG_MIRRORED = "mirrored";
	private static final String TAG_MANIFEST_NAME = "manifest_name";
	private static final String TAG_MIME = "mime";
	private static final String TAG_MTIME = "mtime";
	private static final String TAG_PATH = "path";
	private static final String TAG_TYPE = "type";
	private static final String TAG_ID = "id";
	private static final String TAG_INCOMPLETE = "incomplete";
	private static final String TAG_SIZE = "size";
	private static final String TAG_SYNC_TYPE = "sync_type";
	private static final String TAG_MOUNT_POINT = "mount_point";
	private static final String TAG_ORIGIN_DEVICE = "origin_device";
	private static final String TAG_ORIGIN_DEVICE_ID = "origin_device_id";
	private static final String TAG_NUM_OBJECTS = "num_objects";
	private static final String TAG_STORAGE = "storage";
	private static final String TAG_STORAGE_TOTAL = "total";
	private static final String TAG_STORAGE_USED = "used";
	private static final String TAG_STORAGE_DISPLAY = "display";
	private static final String TAG_REFERRAL_LINK = "referral_link";
	private static final String TAG_DISPLAY_NAME = "display_name";
	private static final String TAG_CTIME = "ctime";
	private static final String TAG_BIRTH_TIME = "birth_time";
	private static final String TAG_ARTIST = "artist";
	private static final String TAG_NMTIME = "n_mtime";
	private static final String TAG_ALBUM_ART = "album_art";

	public String mAccessToken;
	public BitcasaError mBitcasaError = new BitcasaError();
	public ArrayList<FileMetaData> mfiles;
	public boolean mbContainsMirrored = false;
	public int mNumDeleted = 0;
	public AccountInfo mAccountInfo;
	
	public BitcasaParseJSON() {
	}
	
	public boolean readJsonStream(InputStream in) throws IOException, BitcasaException {
		
		JsonReader reader = new JsonReader(new InputStreamReader(in, BitcasaRESTConstants.UTF_8_ENCODING));

		return getResult(reader);
	}
	
	private boolean getResult(JsonReader reader) throws IOException, BitcasaException {
		reader.beginObject();
		while (reader.hasNext()) {
			if (reader.peek() == JsonToken.NULL)
				break;
			String name = reader.nextName();
			if (name.equals(TAG_ERROR)) {
				
				if (reader.peek() != JsonToken.NULL) {
					getResultSecondLevel(reader);
				}
				else {
					reader.skipValue();
				}
			}
			else if (name.equals(TAG_RESULT) && reader.peek() != JsonToken.NULL) {
				//to read result
				getResultSecondLevel(reader);
			}
			else if (name.equals(TAG_DELETED)) {
				
			}
			else {
				reader.skipValue();
			}
		}
		reader.endObject();
		
		if (mBitcasaError.getCode() > 0)
			return false;
		else
			return true;
	}
	
	private void getResultSecondLevel(JsonReader reader) throws IOException{
		reader.beginObject();
		
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals(TAG_ACCESS_TOKEN)) {
				mAccessToken = reader.nextString();
				break;
			}
			else if (name.equals(TAG_ITEMS)){
				//READ ARRAY
				getResultItemsArray(reader);
			}
			else if (name.equals(TAG_NUM_OBJECTS)) {
				mNumDeleted = reader.nextInt();
				break;
			}
			else if (name.equals(TAG_ERROR_CODE)){
				mBitcasaError.setCode(reader.nextInt());
			}
			else if (name.equals(TAG_ERROR_MESSAGE)) {
				mBitcasaError.setMessage(reader.nextString());
			}
			else if (name.equals(TAG_STORAGE)) {
				if (mAccountInfo == null)
					mAccountInfo = new AccountInfo();
				reader.beginObject();
				while (reader.hasNext()) {
					String storageName = reader.nextName();
					if (storageName.equals(TAG_STORAGE_TOTAL))
						mAccountInfo.setStorage_total(reader.nextLong());
					else if (storageName.equals(TAG_STORAGE_DISPLAY))
						mAccountInfo.setStorage_display(reader.nextString());
					else if (storageName.equals(TAG_STORAGE_USED))
						mAccountInfo.setStorage_used(reader.nextLong());
					else
						reader.skipValue();
				}
				reader.endObject();
			}
			else if (name.equals(TAG_DISPLAY_NAME)) {
				if (mAccountInfo == null)
					mAccountInfo = new AccountInfo();
				
				mAccountInfo.setDisplay_name(reader.nextString());
			}
			else if (name.equals(TAG_ID)) {
				if (mAccountInfo == null)
					mAccountInfo = new AccountInfo();
				
				mAccountInfo.setId(reader.nextString());
			}
			else if (name.equals(TAG_REFERRAL_LINK)) {
				if (mAccountInfo == null)
					mAccountInfo = new AccountInfo();
				
				mAccountInfo.setReferralLink(reader.nextString());
			}
			else
				reader.skipValue();
				
		}
		
		reader.endObject();
	
	}
	
	private void getResultItemsArray(JsonReader reader) throws IOException {
		reader.beginArray();
		
		mfiles = new ArrayList<FileMetaData>();
		while (reader.hasNext()) {
			FileMetaData f = readItem(reader);
			if (f != null)
				mfiles.add(f);
		}
		reader.endArray();
	}
	
	private FileMetaData readItem(JsonReader reader) throws IOException {
		FileMetaData file = new FileMetaData();
		
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals(TAG_CATEGORY) && reader.peek() != JsonToken.NULL)
				file.category = reader.nextString();
			else if (name.equals(TAG_NAME))
				file.name = reader.nextString();
			else if (name.equals(TAG_MTIME))
				file.mtime = reader.nextLong();
			else if (name.equals(TAG_PATH))
				file.path = reader.nextString();
			else if (name.equals(TAG_TYPE)) {
				int type = reader.nextInt();
				file.type = (type==0?FileType.BITCASA_TYPE_FILE:FileType.BITCASA_TYPE_FOLDER);
			}
			else if (name.equals(TAG_MIRRORED)) {
				file.mirrored = reader.nextBoolean();
				
				if (file.mirrored)
					mbContainsMirrored = true;
			}
			else if (name.equals(TAG_ALBUM) && reader.peek() != JsonToken.NULL)
				file.album = reader.nextString();
			else if (name.equals(TAG_EXTENSION) && reader.peek() != JsonToken.NULL)
				file.extension = reader.nextString();
			else if (name.equals(TAG_DUPLICATES)) {
				reader.skipValue();
			}
			else if (name.equals(TAG_MANIFEST_NAME) && reader.peek() != JsonToken.NULL)
				file.manifest_name = reader.nextString();
			else if (name.equals(TAG_MIME) && reader.peek() != JsonToken.NULL)
				file.mime = reader.nextString();
			else if (name.equals(TAG_ID) && reader.peek() != JsonToken.NULL)
				file.id = reader.nextString();
			else if (name.equals(TAG_INCOMPLETE) && reader.peek() != JsonToken.NULL)
				file.incomplete = reader.nextBoolean();
			else if (name.equals(TAG_SIZE))
				file.size = reader.nextLong();
			else if (name.equals(TAG_SYNC_TYPE))
				file.sync_type = reader.nextString();
			else if (name.equals(TAG_MOUNT_POINT))
				file.mount_point = reader.nextString();
			else if (name.equals(TAG_DELETED) && reader.peek() != JsonToken.NULL)
				file.deleted = reader.nextBoolean();
			else if (name.equals(TAG_ORIGIN_DEVICE) && reader.peek() != JsonToken.NULL)
				file.origin_device = reader.nextString();
			else if (name.equals(TAG_ORIGIN_DEVICE_ID) && reader.peek() != JsonToken.NULL)
				file.origin_device_id = reader.nextString();
			else if (name.equals(TAG_ALBUM_ART) && reader.peek() != JsonToken.NULL) {
				file.file = readItem(reader);
			}
			else if (name.equals(TAG_CTIME) && reader.peek() != JsonToken.NULL)
				file.c_time = reader.nextLong();
			else if (name.equals(TAG_BIRTH_TIME) && reader.peek() != JsonToken.NULL)
				file.birth_time = reader.nextLong();
			else if (name.equals(TAG_NMTIME) && reader.peek() != JsonToken.NULL)
				file.n_mtime = reader.nextLong();
			else
				reader.skipValue();
						
		}
		
		reader.endObject();
		return file;
	}
	
	
}