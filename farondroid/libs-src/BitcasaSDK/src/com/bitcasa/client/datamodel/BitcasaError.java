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

public class BitcasaError {

	private int mCode = -1;
	private String mMessage;
	
	public BitcasaError() {
		
	}
	
	public BitcasaError(int code, String message) {
		this.mCode = code;
		this.mMessage = message;
	}

	public int getCode() {
		return mCode;
	}

	public void setCode(int Code) {
		this.mCode = Code;
	}

	public String getMessage() {
		return mMessage;
	}

	public void setMessage(String Message) {
		this.mMessage = Message;
	}
	
}
