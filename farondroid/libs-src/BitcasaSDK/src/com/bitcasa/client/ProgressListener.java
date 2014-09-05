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

package com.bitcasa.client;

public interface ProgressListener {
	
	public enum ProgressAction {
		BITCASA_ACTION_DOWNLOAD,
		BITCASA_ACTION_UPLOAD
	}

	public void onProgressUpdate(String file, int percentage, ProgressAction action);
	public void canceled(String file, ProgressAction action);
}
