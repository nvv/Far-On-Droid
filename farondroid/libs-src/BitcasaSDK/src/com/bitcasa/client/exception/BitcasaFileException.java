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

package com.bitcasa.client.exception;

public class BitcasaFileException extends BitcasaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	BitcasaFileException() {
		super("Bitcasa File Exception");
	}
	
	public BitcasaFileException(String message) {
		super(message);
	}
	
	public BitcasaFileException(int code, String message) {
		super(code, message);
	}

}
