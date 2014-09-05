package com.bitcasa.client;

public class BitcasaConstants {

	final static public String EXTRA_ALLOW_ACCESS_RESULT			= "allow_access_result";
	final static public String EXTRA_CLIENT_ID 						= "client_id";
	final static public String EXTRA_AUTHORIZATION_CODE 			= "auth_code";
	final static public String BITCASA_PACKAGE_NAME 				= "com.bitcasa.android";
	final static public String BITCASA_CLASS_NAME 					= "com.bitcasa.android.activities.AllowAccessActivity";
	final static public String BITCASA_ACTION_PERMISSION_REQUEST 	= "com.bitcasa.android.permission.ACTION_PERMISSION_REQUEST";
	
	public static enum AllowAccessResult {
		RESULT_CANCELLED("Cancelled"),
		RESULT_ALLOWED("Allowed"),
		RESULT_ERROR_SESSION_EXPIRED("Error - Session expired"),
		RESULT_ERROR_REQUEST_ERROR("Error - Request error"),
		RESULT_ERROR("Error");
		
		private String mAllowAccessReuslt;
		private AllowAccessResult(String result) {
			mAllowAccessReuslt = result;
		}
		
		public static AllowAccessResult getResult(String result) {
			if(result.equals("Cancelled"))
				return RESULT_CANCELLED;
			else if (result.equals("Allowed"))
				return RESULT_ALLOWED;
			else if (result.equals("Error - Session expired"))
				return RESULT_ERROR_SESSION_EXPIRED;
			else if (result.equals("Error - Request error"))
				return RESULT_ERROR_REQUEST_ERROR;
			else if (result.equals("Error"))
				return RESULT_ERROR;
			else 
				return null;
						
		}
		
		@Override
		public String toString() {
			return mAllowAccessReuslt;
		}
	}
}
