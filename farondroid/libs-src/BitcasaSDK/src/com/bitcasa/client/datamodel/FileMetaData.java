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

package com.bitcasa.client.datamodel;

import com.bitcasa.client.HTTP.BitcasaRESTConstants.FileType;

public class FileMetaData {
	
	/**
	 * category:music_artists, music_albums, music_tracks, photo_albums, photos, documents, videos, everything
	 */
	public String category;
	
	/**
	 * status: null or created when add folder
	 */
	public String status;
	
	/**
	 * name: name of the file
	 */
	public String name;
	
	/**
	 * mirrored: if the folder/file is mirrored
	 */
	public boolean mirrored;
	
	/**
	 * mtime: file modification time
	 */
	public long mtime;
	
	/**
	 * path: file path in Bitcasa
	 */
	public String path;
	
	/**
	 * type: 0 is file; 1 is for folder
	 */
	public FileType type;
	
	/**
	 * extension: file extension
	 */
	public String extension;
	
	/**
	 * manifest_name: Bitcasa file manifest name
	 */
	public String manifest_name;
	
	/**
	 * mime: application/pdf, image, photo, video, audio
	 */
	public String mime;
	
	/**
	 * id: file's identification string
	 */
	public String id;
	
	/**
	 * incomplete: if the file is not a complete file
	 */
	public boolean incomplete;
	
	/**
	 * size: size of the file
	 */
	public long size; 
	
	/**
	 * album name
	 */
	public String album;
	
	public String sync_type;
	
	public String mount_point;
	
	public boolean deleted;
	
	public String origin_device;
	
	public String origin_device_id;
	
	public long c_time;
	
	public long birth_time;
	
	public String artist;
	
	public long n_mtime;
	
	/**
	 * file: null, or music artist detail,
	 */
	public FileMetaData file = null;
	
	public FileMetaData() {
		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("category[").append(category).append("] status[").append(status).append("] name[").append(name).append("] mirrored[")
			.append(mirrored).append("] mtime[").append(mtime).append("] path[").append(path).append("] type[")
			.append(type).append("] extension[").append(extension).append("] manifest_name[").append(manifest_name).append("] mime[").append(mime).append("]")
			
			.append("id[").append(id).append("] incomplete[").append(incomplete).append("] size[").append(size).append("] album[")
			.append(album).append("] sync_type[").append(sync_type).append("] mount_point[").append(mount_point).append("] deleted[")
			.append(deleted).append("] origin_device[").append(origin_device).append("] origin_device_id[").append(origin_device_id).append("] c_time[").append(c_time).append("]")
			.append("birth_time[").append(birth_time).append("] artist[").append(artist).append("] n_mtime[").append(n_mtime).append("]");
		
		if (file != null) {
			sb.append("\n....!!!!!");
			sb.append("category[").append(file.category)
			.append("] status[").append(file.status)
			.append("] name[").append(file.name)
			.append("] mirrored[").append(file.mirrored)
			.append("] mtime[").append(file.mtime)
			.append("] path[").append(file.path)
			.append("] type[").append(file.type)
			.append("] extension[").append(file.extension)
			.append("] manifest_name[").append(file.manifest_name)
			.append("] mime[").append(file.mime).append("]")		
			.append("id[").append(file.id)
			.append("] incomplete[").append(file.incomplete)
			.append("] size[").append(file.size)
			.append("] album[").append(file.album)
			.append("] sync_type[").append(file.sync_type)
			.append("] mount_point[").append(file.mount_point)
			.append("] deleted[").append(file.deleted)
			.append("] origin_device[").append(file.origin_device)
			.append("] origin_device_id[").append(file.origin_device_id)
			.append("] c_time[").append(file.c_time).append("]")
			.append("birth_time[").append(file.birth_time)
			.append("] artist[").append(file.artist)
			.append("] n_mtime[").append(file.n_mtime).append("]!!!!!");
		}
		sb.append("*****");
		
		return sb.toString();
	}
	
}
