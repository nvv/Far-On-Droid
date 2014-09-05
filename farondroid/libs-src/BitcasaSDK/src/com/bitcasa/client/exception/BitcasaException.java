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

import com.bitcasa.client.datamodel.BitcasaError;

public class BitcasaException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	BitcasaException() {
		super("Bitcasa exception");
	}
	
	public BitcasaException(String message) {
		super(message);
	}
	
	public BitcasaException(int code, String message) {
		super("Error code = " + code + " Detail message = " + message);
	}
	
	public BitcasaException(BitcasaError error) {
		super("Error code = " + error.getCode() + " Detail messsage = " + error.getMessage());
	}

}
