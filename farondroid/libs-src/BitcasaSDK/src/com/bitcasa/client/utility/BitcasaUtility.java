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

package com.bitcasa.client.utility;

import java.io.IOException;
import java.util.ArrayList;

import com.bitcasa.client.BitcasaClient;
import com.bitcasa.client.HTTP.BitcasaRESTConstants.FileType;
import com.bitcasa.client.datamodel.FileMetaData;
import com.bitcasa.client.exception.BitcasaException;

public class BitcasaUtility {

	public static final String FOLDER_DEFAULT_ROOT				= "/";
	public static final String FOLDER_MIRRORED_FOLDERS			= "Mirrored Folders";
	public static final String MIRRORED_FOLDERS_SYNC_TYPE 		= "fake";
	public static final String BITCASA_INFINITE_DRIVE 			= "infinite drive";
	public static final String SYNC_TYPE_BACKUP 				= "backup";
	public static final String SYNC_TYPE_SYNC 					= "sync";
	public static final String FOLDER_NO_DEVICE_NAME			= "NO DEVICE NAME";
	
	public static ArrayList<FileMetaData> constructBID(BitcasaClient api, ArrayList<FileMetaData> files, boolean bContainsMirrored) throws IOException, BitcasaException {
		
		if (bContainsMirrored) {
			//remove current mirrored folders
			for (int i = files.size()-1; i>=0; i--) {
				if (files.get(i).mirrored)
					files.remove(i);
			}
			//add mirrored folder
			FileMetaData mirrorfolder = new FileMetaData();
			mirrorfolder.name = FOLDER_MIRRORED_FOLDERS;
			mirrorfolder.type = FileType.BITCASA_TYPE_FOLDER;
			mirrorfolder.sync_type = MIRRORED_FOLDERS_SYNC_TYPE;
			mirrorfolder.mirrored = true;
			mirrorfolder.path = FOLDER_DEFAULT_ROOT + FOLDER_MIRRORED_FOLDERS;
			
			files.add(mirrorfolder);
		}
		
		//Bitcasa infinite drive
		FileMetaData BID = null;
		int indexBID = -1;
		for (int i=0; i<files.size(); i++) {
			if (files.get(i).sync_type.equals(BITCASA_INFINITE_DRIVE)) {
				BID = files.get(i);
				indexBID = i;
				break;
			}
		}
		
		if (BID != null) {
			//request to get infinite drive folders
			ArrayList<FileMetaData> bidlist = api.getList(BID, null, 0, null);
			files.remove(indexBID);
			if (bidlist != null)
				files.addAll(bidlist);
		}
		
		return files;
	}
	
	public static ArrayList<FileMetaData> constructMirrorFolder(ArrayList<FileMetaData> files) throws IOException, BitcasaException {
		
		ArrayList<FileMetaData> mirrorFolders = new ArrayList<FileMetaData>();
		for (int i=0; i<files.size(); i++) {
			//only keep the mirror files
			if (files.get(i).sync_type.equals(BitcasaUtility.SYNC_TYPE_BACKUP) || files.get(i).sync_type.equals(BitcasaUtility.SYNC_TYPE_SYNC)) {
				String origin_device = files.get(i).origin_device;
				if (origin_device == null || origin_device.equals(""))
					origin_device = FOLDER_NO_DEVICE_NAME;
								
				//check if this subfolder already been added
				if (!isFileAlreadyInFolder(origin_device, mirrorFolders)){
					//add mirrored subfolder
					FileMetaData mirrorfolder = new FileMetaData();
					
					mirrorfolder.name = origin_device;
					mirrorfolder.type = FileType.BITCASA_TYPE_FOLDER;
					mirrorfolder.sync_type = MIRRORED_FOLDERS_SYNC_TYPE;
					mirrorfolder.mirrored = true;
					mirrorfolder.mtime = files.get(i).mtime;
					mirrorfolder.path = FOLDER_DEFAULT_ROOT + FOLDER_MIRRORED_FOLDERS + "/" + mirrorfolder.name;
					
					mirrorFolders.add(mirrorfolder);
				}
			}
		}
		
		return mirrorFolders;
	}
	
	private static boolean isFileAlreadyInFolder(String name, ArrayList<FileMetaData> mirrorFolders) {
		for (int i=0; i<mirrorFolders.size(); i++)
			if (mirrorFolders.get(i).name.equals(name))
				return true;
		
		return false;
	}
	
	public static ArrayList<FileMetaData> constructMirrorDevice(String deviceName, ArrayList<FileMetaData> files) throws IOException, BitcasaException {
		
		ArrayList<FileMetaData> mirrorDevices = new ArrayList<FileMetaData>();
		for (int i=0; i<files.size(); i++) {
			FileMetaData f = files.get(i);
			if ((f.sync_type.equals(BitcasaUtility.SYNC_TYPE_BACKUP) || f.sync_type.equals(BitcasaUtility.SYNC_TYPE_SYNC))
					&& f.origin_device.equals(deviceName)) {
				
				mirrorDevices.add(f);
			}
		}
		
		return mirrorDevices;
	}
	
}
