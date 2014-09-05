package com.bitcasa.client.datamodel;

public class Authentication {
	/**
	 * client_id: obtain it from Bitcasa after register your app
	 */
	private String client_id;
	
	/**
	 * secret: obtain it from Bitcasa after register your app
	 */
	private String secret;
	
	/**
	 * access_token
	 */
	private String access_token;
	
	/**
	 * authentication_code
	 */
	private String authorization_code;
	
	public Authentication(String client_id, String secret) {
		this.client_id = client_id;
		this.secret = secret;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getAuthentication_code() {
		return authorization_code;
	}

	public void setAuthorization_code(String authorization_code) {
		this.authorization_code = authorization_code;
	}
	
}
