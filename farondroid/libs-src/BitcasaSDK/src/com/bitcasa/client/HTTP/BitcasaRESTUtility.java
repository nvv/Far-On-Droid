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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;



public class BitcasaRESTUtility {
	
	public String getServerUrl() {
		return BitcasaRESTConstants.SERVER_URL;
	}
	
	public String getRequestUrl(String request, String method, StringBuilder params) {
		StringBuilder url = new StringBuilder();
		
		url.append(getServerUrl())
		.append(BitcasaRESTConstants.VERSION)
		.append(request).append(method)
		.append("?").append(params.toString());
		
		return url.toString();
	}
	
	public String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}
	
	public String urlEncodeSegments(String path) {
		String encodedPath = null;
		StringBuilder sb = new StringBuilder();
		try {
			if (path.equals(BitcasaRESTConstants.FORESLASH)) {
				encodedPath = BitcasaRESTConstants.FORESLASH;
			} else {
				String[] segments = path.split(BitcasaRESTConstants.FORESLASH);
				final int count = segments.length;
				for (int i = 0; i < count; i++) {
					sb.append(URLEncoder.encode(segments[i], BitcasaRESTConstants.UTF_8_ENCODING).replace("+", "%20")).append(BitcasaRESTConstants.FORESLASH);;
				}
				encodedPath = sb.toString();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodedPath;
	}
	
}
