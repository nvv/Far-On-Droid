package com.bitcasa.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

import com.bitcasa.client.HTTP.BitcasaRESTConstants;
import com.bitcasa.client.HTTP.BitcasaRESTUtility;
import com.bitcasa.client.HTTP.CustomMultiPartEntityUpload;
import com.bitcasa.client.HTTP.HttpDeleteWithBody;
import com.bitcasa.client.HTTP.BitcasaRESTConstants.Category;
import com.bitcasa.client.HTTP.BitcasaRESTConstants.CollisionResolutions;
import com.bitcasa.client.HTTP.BitcasaRESTConstants.Depth;
import com.bitcasa.client.HTTP.BitcasaRESTConstants.FileOperation;
import com.bitcasa.client.HTTP.BitcasaRESTConstants.FileType;
import com.bitcasa.client.ParseJSON.BitcasaParseJSON;
import com.bitcasa.client.ProgressListener.ProgressAction;
import com.bitcasa.client.datamodel.AccountInfo;
import com.bitcasa.client.datamodel.Authentication;
import com.bitcasa.client.datamodel.BitcasaError;
import com.bitcasa.client.datamodel.FileMetaData;
import com.bitcasa.client.exception.BitcasaAuthenticationException;
import com.bitcasa.client.exception.BitcasaClientException;
import com.bitcasa.client.exception.BitcasaException;
import com.bitcasa.client.exception.BitcasaFileException;
import com.bitcasa.client.exception.BitcasaFileSystemException;
import com.bitcasa.client.exception.BitcasaRequestErrorException;
import com.bitcasa.client.exception.BitcasaServerException;
import com.bitcasa.client.utility.BitcasaUtility;
import com.google.gson.stream.JsonReader;

public class BitcasaClient {
	private static final String TAG = BitcasaClient.class.getSimpleName();
	
	private BitcasaRESTUtility mBitcasaRESTUtility;
	private Authentication mAuthentication;
	private FileMetaData mBitcasaInfiniteDrive = null;
	
	/**
	 * Initiate BitcasaAPI
	 * @param client_id
	 * @param client_secret
	 */
	public BitcasaClient(String client_id, String client_secret) {
		
		mAuthentication = new Authentication(client_id, client_secret);
		mBitcasaRESTUtility = new BitcasaRESTUtility();
	} 
	
	/**
	 * Get Bitcasa Authorization URL
	 * @param client_id
	 * @return null or authorization url
	 */
	public String getAuthorizationUrl(String client_id) {
		
		StringBuilder params = new StringBuilder();    	
		params.append(BitcasaRESTConstants.PARAM_CLIENT_ID).append("=")
		.append(client_id)
		.append("&")
		.append(BitcasaRESTConstants.PARAM_REDIRECT).append("=")
		.append(BitcasaRESTConstants.REDIRECT_URI);
		
		String url = mBitcasaRESTUtility.getRequestUrl(BitcasaRESTConstants.METHOD_OAUTH2, BitcasaRESTConstants.METHOD_AUTHENTICATE, params);

		return url;
	}
   
    /**
     * Obtain access token from client secret and authorization code 
     * @param secret
     * @param authorization_code
     * @return null or Bitcasa authentication code
     * @throws IOException
     * @throws BitcasaException
     */
    public String getAccessToken(String secret, String authorization_code) throws IOException, BitcasaException {
	
    	if (mAuthentication.getAccess_token() != null)
    		return mAuthentication.getAccess_token();
    	
		StringBuilder params = new StringBuilder();    	
		params.append(BitcasaRESTConstants.PARAM_SECRET).append("=")
		.append(secret)
		.append("&")
		.append(BitcasaRESTConstants.PARAM_CODE).append("=")
		.append(authorization_code);
		
		String url = mBitcasaRESTUtility.getRequestUrl(BitcasaRESTConstants.METHOD_OAUTH2, BitcasaRESTConstants.METHOD_ACCESS_TOKEN, params);
		
		HttpsURLConnection connection = null;
		OutputStream os = null;
		InputStream is = null;
		String accesstoken = null;
		try {
			connection = (HttpsURLConnection) new URL(url)
					.openConnection();
			connection.setRequestMethod(BitcasaRESTConstants.REQUEST_METHOD_GET);
			// read response code
			final int responseCode = connection.getResponseCode();			
			
			if (responseCode == HttpsURLConnection.HTTP_OK) {				
				is = connection.getInputStream();
			}
			else if (responseCode == HttpsURLConnection.HTTP_BAD_REQUEST) {				
				is = connection.getErrorStream();
			}
			
			BitcasaParseJSON parser = new BitcasaParseJSON();
			if (responseCode == HttpsURLConnection.HTTP_OK && parser.readJsonStream(is)) {
				accesstoken = parser.mAccessToken;
				mAuthentication.setAccess_token(parser.mAccessToken);
			}
			else
			if (parser.mBitcasaError.getCode() > 0)
				throw new BitcasaAuthenticationException(parser.mBitcasaError);
			
			//1020, 1021
			if (responseCode == HttpsURLConnection.HTTP_OK) {				
				//do nothing
			}
			else if (responseCode == HttpsURLConnection.HTTP_BAD_REQUEST) {
				throw new BitcasaAuthenticationException(1020, "Authorization code not recognized");
			}
			else
				throw new BitcasaAuthenticationException(Integer.toString(responseCode));
			
		}catch (IOException ioe) {
				if (ioe.getMessage().contains("authentication challenge"))
					throw new BitcasaAuthenticationException(1020, "Authorization code not recognized");
				else if (ioe != null)
					ioe.printStackTrace();
			
		} finally {
			if (os != null) {
				os.close();
			}
			if (is != null) {
				is.close();
			}
			if (connection != null)
				connection.disconnect();
		}
		
		return accesstoken;
	}
    
       
    /**
     * Add or create new folder in Bitcasa Infinite Drive
     * @param newfolderName
     * @param toFolder
     * @return null or Bitcasa FileMetaData of just created folder
     * @throws IOException
     * @throws BitcasaException
     */
    public FileMetaData addFolder(String newfolderName, FileMetaData toFolder)  throws IOException, BitcasaException {
    	
    	validateFileFolderName(newfolderName, FileType.BITCASA_TYPE_FOLDER);
    	
    	if (toFolder != null && toFolder.mirrored)
    		throw new BitcasaRequestErrorException(2009, "Cannot create folder. Specificed location is read only");
    	
    	StringBuilder params = new StringBuilder();    	
		params.append(BitcasaRESTConstants.PARAM_ACCESS_TOKEN).append("=")
		.append(mAuthentication.getAccess_token());
		
		String method = BitcasaRESTConstants.FORESLASH;
		
		if (toFolder == null) {			
    		if (mBitcasaInfiniteDrive == null)
    			getList(null, null, 0, null);
    		
    		toFolder = mBitcasaInfiniteDrive;	
		}
		
		if (toFolder != null && toFolder.path != null)
			method = toFolder.path;
		
		String url = mBitcasaRESTUtility.getRequestUrl(BitcasaRESTConstants.METHOD_FOLDERS, method, params);
		String body = BitcasaRESTConstants.BODY_FOLDERNAME + "=" + newfolderName;
		
		HttpsURLConnection connection = null;
		OutputStream os = null;
		InputStream is = null;
		ArrayList<FileMetaData> result = null;
		
		try {
			connection = (HttpsURLConnection) new URL(url)
					.openConnection();
			connection.setRequestMethod(BitcasaRESTConstants.REQUEST_METHOD_POST);
			os = connection.getOutputStream();
			os.write(body.getBytes());
			
			final int responseCode = connection.getResponseCode();
			
			Log.d(TAG, "Response code from addFolder: " + responseCode);
			if (responseCode == HttpsURLConnection.HTTP_OK) {				
				is = connection.getInputStream();
			}
			else
				is = connection.getErrorStream();
			
			BitcasaParseJSON parser = new BitcasaParseJSON();
			if (parser.readJsonStream(is)) {
				result = parser.mfiles;
			}
			else if (parser.mBitcasaError.getCode() > 0)
				throw new BitcasaServerException(parser.mBitcasaError);
			
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				// do nothing
			}
			else if (responseCode == HttpsURLConnection.HTTP_BAD_REQUEST) {
				throw new BitcasaRequestErrorException(new BitcasaError(2031, "Folder name is invalid.")); //2031
			}
			else if (responseCode == HttpsURLConnection.HTTP_FORBIDDEN) {
				throw new BitcasaRequestErrorException(new BitcasaError(2009, "Cannot create folder. Specificed location is read only"));	//2009
			}
			else if (responseCode == HttpsURLConnection.HTTP_NOT_FOUND) {
				throw new BitcasaRequestErrorException(new BitcasaError(2002, "Folder does not exist.")); //2002
			}
			else if (responseCode == HttpsURLConnection.HTTP_CONFLICT) {
				throw new BitcasaRequestErrorException(new BitcasaError(2014, "Name conflict creating folder.")); //2014
			} 
			else if (responseCode == HttpsURLConnection.HTTP_INTERNAL_ERROR) {
				throw new BitcasaRequestErrorException(new BitcasaError(2024, "Failed to save changes.")); //2020,2022,2023,2024,2025
			} 
			else {
				throw new BitcasaRequestErrorException(Integer.toString(responseCode));
			}
		} catch (IOException ioe) {
			if (ioe.getMessage().contains("authentication challenge"))
				throw new BitcasaAuthenticationException(1020, "Authorization code not recognized");
			else if (ioe != null)
				ioe.printStackTrace();
		} finally {
			if (os != null) {
				os.close();
			}
			if (is != null) {
				is.close();
			}
			if (connection != null)
				connection.disconnect();
		}
		
		if (result != null && result.size() >= 1)
			return result.get(0);
		else 
			return null;
	}
    
    /**
     * Get list of files from Bitcasa Infinite Drive
     * @param folder
     * @param depth
     * @param latest
     * @param category
     * @return null or ArrayList of Bitcasa FileMetaData
     * @throws IOException
     * @throws BitcasaException
     */
    public ArrayList<FileMetaData> getList(FileMetaData folder, Depth depth, int latest, Category category) throws IOException, BitcasaException {
    	
    	ArrayList<FileMetaData> files = null;
    	String path;
    	if (folder == null)
    		path = BitcasaRESTConstants.FORESLASH;
    	else if (folder.path.startsWith(BitcasaUtility.FOLDER_DEFAULT_ROOT+BitcasaUtility.FOLDER_MIRRORED_FOLDERS))
    		path = BitcasaRESTConstants.FORESLASH;
    	else 
    		path = folder.path;
		
		StringBuilder params = new StringBuilder();    	
		params.append(BitcasaRESTConstants.PARAM_ACCESS_TOKEN).append("=")
		.append(mAuthentication.getAccess_token());
		
		if (depth != null) {
			params.append("&");
			params.append(BitcasaRESTConstants.PARAM_DEPTH).append("=")
			.append(depth==Depth.CURRENT_CHILDREN?1:0);
		}
		
		if (latest > 0) {
			params.append("&");
			params.append(BitcasaRESTConstants.PARAM_LATEST).append("=")
			.append(latest);
		}
		
		if (category != null) {
			params.append("&");
			params.append(BitcasaRESTConstants.PARAM_CATEGORY).append("=")
			.append(category);
		}
		
		
		
		String url = mBitcasaRESTUtility.getRequestUrl(BitcasaRESTConstants.METHOD_FOLDERS, path, params);
					
		
		Log.d(TAG, "getList URL: " + url);
		HttpsURLConnection connection = null;
		InputStream is = null;
		JsonReader reader = null;
		BitcasaParseJSON parser = null;
		
		try {
			connection = (HttpsURLConnection) new URL(url)
					.openConnection();
			connection.setRequestMethod(BitcasaRESTConstants.REQUEST_METHOD_GET);
			
			// read response code
			final int responseCode = connection.getResponseCode();
			Log.d(TAG, "getList response The response code is: "
					+ responseCode);
			
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				is = connection.getInputStream();
			} 
			else
				is = connection.getErrorStream();
			
			parser = new BitcasaParseJSON();
			if (parser.readJsonStream(is))
				files = parser.mfiles;
			else if (parser.mBitcasaError.getCode() > 0)
				throw new BitcasaServerException(parser.mBitcasaError);	
			
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				//do nothing
			}
			else if (responseCode == HttpsURLConnection.HTTP_NOT_FOUND)
				throw new BitcasaRequestErrorException(new BitcasaError(2002, "Folder does not exist.")); //2002
			else if (responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
				throw new BitcasaAuthenticationException(1020, "Authorization code not recognized");
			} 
			else
				throw new BitcasaRequestErrorException(Integer.toString(responseCode)); 
			
		} catch (IOException ioe) {
			if (ioe.getMessage().contains("authentication challenge"))
				throw new BitcasaAuthenticationException(1020, "Authorization code not recognized");
			else if (ioe != null)
				ioe.printStackTrace();
		} catch (Exception e) {
			if (e != null)
				e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
			
			if (is != null) {
				is.close();
			}
			
		}
		
		if (connection != null)
			connection.disconnect();
		
		if (category == null || category == Category.EVERYTHING) {
			if (folder == null && path.equals(BitcasaRESTConstants.FORESLASH) && parser != null && parser.mbContainsMirrored && files != null) {
				//get Infinite Drive
				for (FileMetaData f:files){
					if (f.sync_type.equals(BitcasaUtility.BITCASA_INFINITE_DRIVE)) {
						mBitcasaInfiniteDrive = f;
						break;
					}
				}
				
				BitcasaUtility.constructBID(this, files, parser.mbContainsMirrored);			
			}
			else if (folder != null && path.equals(BitcasaRESTConstants.FORESLASH) && folder.name.equals(BitcasaUtility.FOLDER_MIRRORED_FOLDERS)){
				files = BitcasaUtility.constructMirrorFolder(files);
			}
			else if (folder != null && path.equals(BitcasaRESTConstants.FORESLASH) && folder.path.startsWith(BitcasaUtility.FOLDER_DEFAULT_ROOT+BitcasaUtility.FOLDER_MIRRORED_FOLDERS)) {
				files = BitcasaUtility.constructMirrorDevice(folder.name, files);
			}
		}
		
		return files;
	}
    
    /**
     * Download a file from Bitcasa Infinite Drive
     * @param file - Bitcasa FileMetaData with valid bitcasa file path and file name
     * @param range - Any valid content range. No less than 0, no greater than the filesize.
     * @param indirect - Boolean. Default 0. When true, responds with a one time url
     * @param localDestination - device file location with file path and name
     * @param listener - to listen to the file download progress
     * @throws BitcasaException
     * @throws IOException
     */
    public void downloadFile(FileMetaData file, long range, boolean indirect, String localDestination, ProgressListener listener) throws BitcasaException, InterruptedException, IOException {
    	if (localDestination == null)
    		throw new BitcasaFileException("Invalid local destination.");
    	
    	File local = new File(localDestination);

        if (local.exists()) {
            local.delete();
        }

        if (!local.exists()) {
			if (!local.getParentFile().mkdirs() && !local.getParentFile().exists())
				throw new BitcasaException(9007, "Appliation not authorized to perform this action");
		}
    	else {
    		throw new BitcasaFileException("File already existed.");
    	}
    	
    	//create file
    	local.createNewFile();
    	
    	if (mAuthentication.getAccess_token() == null)
			throw new BitcasaAuthenticationException(1020, "Authorization code not recognized");
    	else if (file.type == FileType.BITCASA_TYPE_FOLDER)
    		throw new BitcasaClientException("Sorry, we can only download a file right now.");
    	
		StringBuilder method = new StringBuilder();
		if (file.id != null && file.id.length() > 0)
			method.append(BitcasaRESTConstants.FORESLASH).append(file.id);
		
		method.append(BitcasaRESTConstants.FORESLASH).append(URLEncoder.encode(file.name, BitcasaRESTConstants.UTF_8_ENCODING));
		
		StringBuilder params = new StringBuilder();    	
		params.append(BitcasaRESTConstants.PARAM_ACCESS_TOKEN).append("=")
		.append(mAuthentication.getAccess_token());
		
		String url;
		if (file.path != null && file.path.length() >0 )
			params.append("&").append(BitcasaRESTConstants.PARAM_PATH).append("=").append(file.path);
		
		if (indirect)
			params.append("&").append(BitcasaRESTConstants.PARAM_INDIRECT).append("=").append(indirect?"1":"0");
		
		url = mBitcasaRESTUtility.getRequestUrl(BitcasaRESTConstants.METHOD_FILES, method.toString(), params);
		
		HttpsURLConnection connection = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		
			Log.d(TAG, "downloadFile url: " + url);
			try {
				connection = (HttpsURLConnection) new URL(url.toString()).openConnection();
			
				connection.setRequestProperty(BitcasaRESTConstants.HEADER_RANGE, "bytes=" + range + "-");
				connection.setDoInput(true);
				
				// check response code first
				int responseCode;
				responseCode = connection.getResponseCode();
				Log.d(TAG, "The response code of downloadFile is: " + responseCode);
				if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_PARTIAL) {

					// prepare for writing to file
					bis = new BufferedInputStream(connection.getInputStream());
					fos = (range == 0) ? new FileOutputStream(local) : new FileOutputStream(local, true);	// if file exists append
					bos = new BufferedOutputStream(fos);
					
					// start writing
					byte[] data = new byte[1024];
					int x = 0;
					// use for progress update
					BigInteger fileSize = new BigInteger(Long.toString(file.size));
					BigInteger dataReceived = BigInteger.valueOf(range);
					long progressUpdateTimer = System.currentTimeMillis();
					while ((x = bis.read(data, 0, 1024)) >= 0) {
						if (Thread.currentThread().isInterrupted()) {
							listener.canceled(file.name, ProgressAction.BITCASA_ACTION_DOWNLOAD);
							break;
						}
						bos.write(data, 0, x);
						dataReceived = dataReceived.add(BigInteger.valueOf(x));
						// update progress
						if (listener != null && (System.currentTimeMillis() - progressUpdateTimer) > BitcasaRESTConstants.PROGRESS_UPDATE_INTERVAL) {
							int percentage = dataReceived.multiply(BigInteger.valueOf(100)).divide(fileSize).intValue();
							listener.onProgressUpdate(file.name, percentage, ProgressAction.BITCASA_ACTION_DOWNLOAD);
							progressUpdateTimer = System.currentTimeMillis();
						}
						
						// make sure everything is written to the file so we can compare the size later
						bos.flush();
					}
					
					// make sure that we did download the whole file
					Log.d(TAG, "local file size: " + local.length() + ", file size should be: " + fileSize);
				}
				else 
				if (responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
					throw new BitcasaAuthenticationException(1020, "Authorization code not recognized");
				}
				else if (responseCode == HttpsURLConnection.HTTP_NOT_FOUND)
					throw new BitcasaRequestErrorException(2003, "File does not exist."); //2003,3001,3003
				else if (responseCode == 416)
					throw new BitcasaRequestErrorException(3004, "Range request is invalid."); //3004
				
			} catch (IOException ioe) {
				if (ioe.getMessage().contains("authentication challenge"))
					throw new BitcasaAuthenticationException(1020, "Authorization code not recognized");
				else if (ioe != null)
					ioe.printStackTrace();
			}
			finally {
				
				if (bos != null)
					bos.close();
				if (fos != null)
					fos.close();
				if (bis != null)
					bis.close();
				
				if(Thread.interrupted()) {
					local.delete();
				}
				
				if (connection != null)
					connection.disconnect();
			}
		
    }
    
    /**
     * Upload a file to Bitcasa Infinite Drive
     * @param folder
     * @param sourcefile
     * @param cr
     * @param indirect
     * @param listener
     * @return null or Bitcasa FileMetaData of uploaded file
     * @throws IOException
     * @throws InterruptedException
     * @throws BitcasaException
     */
    public FileMetaData uploadFile(FileMetaData folder, String sourcefile, CollisionResolutions cr, boolean indirect, ProgressListener listener) throws IOException, InterruptedException, BitcasaException {
    	
    	if (folder != null && folder.mirrored)
    		throw new BitcasaRequestErrorException(2009, "Cannot create folder. Specificed location is read only.");
    	
    	File sourceFile = new File(sourcefile); 
    	if (!sourceFile.exists() || !sourceFile.canRead()) {
    		throw new BitcasaClientException("Unable to read file: " + sourcefile);
		}
    	
    	if (folder == null)	{//infinite drive
    		if (mBitcasaInfiniteDrive == null)
    			getList(null, null, 0, null);
    		
    		folder = mBitcasaInfiniteDrive;
    	}
    	
    	long start = System.currentTimeMillis();
    	ArrayList<FileMetaData> files = null;
    	StringBuilder method = new StringBuilder();
    	String filename = sourceFile.getName();
    	
		method.append(folder.path);
		
		StringBuilder params = new StringBuilder();    	
		params.append(BitcasaRESTConstants.PARAM_ACCESS_TOKEN).append("=")
		.append(mAuthentication.getAccess_token());
		params.append("&").append(BitcasaRESTConstants.PARAM_EXISTS).append("=");
		
		if (cr == null)
			params.append(BitcasaRESTConstants.EXISTS_RENAME);
		else {
			switch(cr) {
			case FAIL:
				params.append(BitcasaRESTConstants.EXISTS_FAIL);
				break;
			case OVERWRITE:
				params.append(BitcasaRESTConstants.EXISTS_OVERWRITE);
				break;
			case RENAME:
				default:
					params.append(BitcasaRESTConstants.EXISTS_RENAME);
					break;
			}
		}
		
		if (indirect)
			params.append("&").append(BitcasaRESTConstants.PARAM_INDIRECT).append("=").append(indirect?"1":"0");
		
		String urlRequest = mBitcasaRESTUtility.getRequestUrl(BitcasaRESTConstants.METHOD_FILES, method.toString(), params);
		Log.d(TAG, "uploadFile url: " + urlRequest);
		
		HttpClient httpClient;
		HttpContext localContext;
		InputStream is = null;
		
		try {
			httpClient = new DefaultHttpClient();
			localContext = new BasicHttpContext();
			HttpParams httpparams = httpClient.getParams();
			httpparams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, Integer.valueOf(300000));
			httpparams.setParameter(CoreConnectionPNames.SO_TIMEOUT, Integer.valueOf(300000));
			HttpPost post = new HttpPost(urlRequest);
			post.setHeader(BitcasaRESTConstants.HEADER_FILE, URLEncoder.encode(filename, BitcasaRESTConstants.UTF_8_ENCODING));
			post.setParams(httpparams);
	
			CustomMultiPartEntityUpload entity = new CustomMultiPartEntityUpload(
					HttpMultipartMode.BROWSER_COMPATIBLE, sourcefile, sourceFile.length(),
					listener);
	
			entity.addPart(BitcasaRESTConstants.BODY_FILE, new FileBody(sourceFile));
			post.setEntity(entity);
	
			HttpResponse response = httpClient.execute(post, localContext);
			final int responseCode = response.getStatusLine().getStatusCode();
			Log.d(TAG, "Response code on upload: " + responseCode);
			
			if (response.getEntity().getContentLength() > 0){
				is = response.getEntity().getContent(); 
				listener.onProgressUpdate(sourcefile, 100, ProgressAction.BITCASA_ACTION_UPLOAD);
				BitcasaParseJSON parser = new BitcasaParseJSON();
				if (parser.readJsonStream(is))
					files = parser.mfiles;
			}
			else 
				Log.d(TAG, "upload getcontentLength is less or equal to 0");
			
			if (responseCode == HttpsURLConnection.HTTP_OK) {	
				//do nothing
			} 
			else if (responseCode == HttpsURLConnection.HTTP_PAYMENT_REQUIRED) 
				throw new BitcasaRequestErrorException(new BitcasaError(1050, "Your account is over quota. Delete some files or upgrade.")); //1050
			else if (responseCode == HttpsURLConnection.HTTP_NOT_FOUND) 
				throw new BitcasaRequestErrorException(new BitcasaError(2002, "Folder does not exist."));	//2002
			else if (responseCode == HttpsURLConnection.HTTP_FORBIDDEN) 
				throw new BitcasaRequestErrorException(new BitcasaError(2004, "Cannot upload. Specified destination is read only.")); //2004
			else if (responseCode == HttpsURLConnection.HTTP_CONFLICT) 	
				throw new BitcasaRequestErrorException(new BitcasaError(2015, "Name conflict on upload."));	//2015
			else if (responseCode == HttpsURLConnection.HTTP_INTERNAL_ERROR) 
				throw new BitcasaRequestErrorException(new BitcasaError(2021, "Failed to save change.")); //2021,2023,2025
			else if (responseCode == HttpsURLConnection.HTTP_BAD_REQUEST) 
				throw new BitcasaRequestErrorException(new BitcasaError(2030, "File name is invalid."));	//2030
			else if (responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
				throw new BitcasaAuthenticationException(1020, "Authorization code not recognized");
			} else {
				throw new BitcasaRequestErrorException(Integer.toString(responseCode));
			}
			long end = System.currentTimeMillis();
			Log.d(TAG, "Upload end: " + end);
			Log.d(TAG, "Total time: " + (end - start));
		}
		catch (IOException ioe) {
			if (ioe.getMessage().contains("authentication challenge"))
				throw new BitcasaAuthenticationException(1020, "Authorization code not recognized");
			else if (ioe != null)
				ioe.printStackTrace();
		}
		finally {
		
			if (is != null)
				is.close();
		}
           
		if (files != null && files.size() >= 1)
			return files.get(0);
		else
			return null; 
            
    }

    public String getDownloadLink(FileMetaData file) throws Exception {
        StringBuilder method = new StringBuilder();
        if (file.id != null && file.id.length() > 0)
            method.append(BitcasaRESTConstants.FORESLASH).append(file.id);

        method.append(BitcasaRESTConstants.FORESLASH).append(URLEncoder.encode(file.name, BitcasaRESTConstants.UTF_8_ENCODING));

        StringBuilder params = new StringBuilder();
        params.append(BitcasaRESTConstants.PARAM_ACCESS_TOKEN).append("=")
                .append(mAuthentication.getAccess_token());

        params.append("&").append(BitcasaRESTConstants.PARAM_PATH).append("=").append(file.path);
        return mBitcasaRESTUtility.getRequestUrl(BitcasaRESTConstants.METHOD_FILES, method.toString(), params);
    }

    /**
     * Delete a file or a folder from Bitcasa Infinite Drive
     * @param file
     * @return null or Bitcasa FileMetaData of deleted file when successful.
     * @throws BitcasaRequestErrorException
     * @throws IOException
     * @throws BitcasaException
     */
    public FileMetaData deleteFile(FileMetaData file) throws BitcasaRequestErrorException, IOException, BitcasaException {
    	
    	if (file == null || (file.mirrored))
    		throw new BitcasaRequestErrorException(2009, "Cannot create folder. Specificed location is read only.");
    	
    	ArrayList<FileMetaData> result = null;
    	
    	String request = BitcasaRESTConstants.METHOD_FILES;
    	if (file.type == FileType.BITCASA_TYPE_FOLDER)
    		request = BitcasaRESTConstants.METHOD_FOLDERS;
    	
    	String body = BitcasaRESTConstants.BODY_PATH + "=" + file.path;
    	
	
		StringBuilder params = new StringBuilder();    	
		params.append(BitcasaRESTConstants.PARAM_ACCESS_TOKEN).append("=")
		.append(mAuthentication.getAccess_token());
		
		String url = mBitcasaRESTUtility.getRequestUrl(request, "", params);
		

		InputStream is = null;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response;
		HttpDeleteWithBody httpDelete;

		try {
			httpDelete = new HttpDeleteWithBody(url.toString());
			httpDelete.addHeader(BitcasaRESTConstants.HEADER_CONTENT_TYPE, 
					BitcasaRESTConstants.HEADER_CONTENT_TYPE_APP_URLENCODED);
			httpDelete.addHeader(BitcasaRESTConstants.HEADER_ACCEPT_CHARSET, BitcasaRESTConstants.UTF_8_ENCODING);
			    
			StringEntity se = new StringEntity(body);
            httpDelete.setEntity(se);
            response = client.execute(httpDelete);
            final int responseCode = response.getStatusLine().getStatusCode();
			
            is = response.getEntity().getContent();             
			BitcasaParseJSON parser = new BitcasaParseJSON();
			if (parser.readJsonStream(is)) {
				result = parser.mfiles;
			}
			else if (parser.mBitcasaError.getCode() > 0)
				throw new BitcasaServerException(parser.mBitcasaError);
			
            Log.d(TAG, "Response code from delete folder: " + responseCode);
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				// do nothing
			}
			else if (responseCode == HttpsURLConnection.HTTP_NOT_FOUND) {
				if (file.type == FileType.BITCASA_TYPE_FILE)
					throw new BitcasaRequestErrorException(new BitcasaError(2003, "File does not exist.")); //2003
				else
					throw new BitcasaRequestErrorException(new BitcasaError(2002, "Folder does not exist.")); //2002
			}
			else if (responseCode == HttpsURLConnection.HTTP_FORBIDDEN) {
				throw new BitcasaRequestErrorException(new BitcasaError(2008, "Cannot delete. Specified location is read only.")); //2008,2026
			}
			else if (responseCode == HttpsURLConnection.HTTP_INTERNAL_ERROR) {
				throw new BitcasaRequestErrorException(new BitcasaError(2024, "Failed to save changes.")); //2022,2023,2024,2025
			} 
			else
				throw new BitcasaRequestErrorException(Integer.toString(responseCode));
			
		}
		catch (IOException ioe) {
				if (ioe.getMessage().contains("authentication challenge"))
					throw new BitcasaAuthenticationException(1020, "Authorization code not recognized");
				else if (ioe != null)
					ioe.printStackTrace();
		} finally {
			
				if (is != null) {
					is.close();
			}
			
		}
		
		if (result != null && result.size() >= 1)
			return result.get(0);
		else
			return null;
    }    
    
    /**
     * Copy a file or a folder from one location to another within Bitcasa Infinite Drive
     * @param from
     * @param toFolder
     * @param cr
     * @return null or Bitcasa FileMetaData of copied file when success.
     * @throws IOException
     * @throws BitcasaException
     */
    public FileMetaData copy(FileMetaData from, FileMetaData toFolder, CollisionResolutions cr) throws IOException, BitcasaException {
    	if (toFolder != null && toFolder.mirrored)
    		throw new BitcasaRequestErrorException(2006, "Cannot Copy. Specified location is read only.");
    	
    	if (mBitcasaInfiniteDrive == null)
    		getList(null, null, 0, null);
    	
    	String toFoderPath = mBitcasaInfiniteDrive.path;
    	if (toFolder != null)
    		toFoderPath = toFolder.path;
    	return copyMoveRename(FileOperation.COPY, from, toFoderPath, cr);
    }
    
    /**
     * Move a file or a folder from one location to another within Bitcasa Infinite Drive
     * @param from
     * @param toFolder
     * @param cr
     * @return null or Bitcasa FileMetaData of moved file when success.
     * @throws IOException
     * @throws BitcasaException
     */
    public FileMetaData move(FileMetaData from, FileMetaData toFolder, CollisionResolutions cr) throws IOException, BitcasaException {
    	if (from == null || from.mirrored || (toFolder != null && toFolder.mirrored))
    		throw new BitcasaRequestErrorException(2005, "Cannot Move. Specified location is read only.");
    	
    	if (mBitcasaInfiniteDrive == null)
    		getList(null, null, 0, null);
    	
    	String toFoderPath = mBitcasaInfiniteDrive.path;
    	if (toFolder != null)
    		toFoderPath = toFolder.path;
    	
    	return copyMoveRename(FileOperation.MOVE, from, toFoderPath, cr);
  
    }
    
    /**
     * Rename a file or a folder within Bitcasa Infinite Drive
     * @param file
     * @param newName
     * @param cr
     * @return null or Bitcasa FileMetaData of renamed file when success.
     * @throws IOException
     * @throws BitcasaException
     */
    public FileMetaData rename(FileMetaData file, String newName, CollisionResolutions cr) throws IOException, BitcasaException {
    	
    	validateFileFolderName(newName, file.type);
    	
    	if (file == null || file.mirrored)
    		throw new BitcasaRequestErrorException(2007, "Cannot Rename. Specified location is read only.");
    	
    	return copyMoveRename(FileOperation.RENAME, file, newName, cr);
    }
    
    private FileMetaData copyMoveRename(FileOperation operationType, FileMetaData file, String filename, CollisionResolutions cr) throws BitcasaRequestErrorException, IOException, BitcasaAuthenticationException {
    	
    	ArrayList<FileMetaData> result = null;
    	
    	String request = BitcasaRESTConstants.METHOD_FILES;
    	if (file.type == FileType.BITCASA_TYPE_FOLDER)
    		request = BitcasaRESTConstants.METHOD_FOLDERS;
    	
		StringBuilder params = new StringBuilder();    	
		params.append(BitcasaRESTConstants.PARAM_ACCESS_TOKEN).append("=")
		.append(mAuthentication.getAccess_token());
		
		StringBuilder body = new StringBuilder();
		body.append(BitcasaRESTConstants.BODY_FROM).append("=").append(file.path);
		
		switch (operationType) {
    	case COPY:
    		params.append("&");
    		params.append(BitcasaRESTConstants.PARAM_OPERATION).append("=")
    		.append(BitcasaRESTConstants.OPERATION_COPY);
    		
    		body.append("&");
    		body.append(BitcasaRESTConstants.BODY_TO).append("=").append(URLEncoder.encode(filename, BitcasaRESTConstants.UTF_8_ENCODING));
    		
    		break;
    	case MOVE:
    		params.append("&");
    		params.append(BitcasaRESTConstants.PARAM_OPERATION).append("=")
    		.append(BitcasaRESTConstants.OPERATION_MOVE);
    		
    		body.append("&");
    		body.append(BitcasaRESTConstants.BODY_TO).append("=").append(URLEncoder.encode(filename, BitcasaRESTConstants.UTF_8_ENCODING));
    		break;
    	case RENAME:
    		params.append("&");
    		params.append(BitcasaRESTConstants.PARAM_OPERATION).append("=")
    		.append(BitcasaRESTConstants.OPERATION_RENAME);
    		
    		body.append("&");
    		body.append(BitcasaRESTConstants.BODY_FILENAME).append("=").append(URLEncoder.encode(filename, BitcasaRESTConstants.UTF_8_ENCODING));
    		
    		break;
    		default:
    			return null;
    	}
		
		switch(cr) {
		case FAIL:
			body.append("&");
    		body.append(BitcasaRESTConstants.BODY_EXISTS).append("=").append(BitcasaRESTConstants.EXISTS_FAIL);
			break;
		case OVERWRITE:
			body.append("&");
    		body.append(BitcasaRESTConstants.BODY_EXISTS).append("=").append(BitcasaRESTConstants.EXISTS_OVERWRITE);
			break;
		case RENAME:
			default:
				body.append("&");
	    		body.append(BitcasaRESTConstants.BODY_EXISTS).append("=").append(BitcasaRESTConstants.EXISTS_RENAME);
			break;
		}
		
		String url = mBitcasaRESTUtility.getRequestUrl(request, "", params);
		
		HttpsURLConnection connection = null;
		OutputStream os = null;
		InputStream is = null;
		try {
			connection = (HttpsURLConnection) new URL(url.toString())
					.openConnection();
			connection.setRequestMethod(BitcasaRESTConstants.REQUEST_METHOD_POST);
			os = connection.getOutputStream();
			os.write(body.toString().getBytes());
			
			final int responseCode = connection.getResponseCode();
			Log.d(TAG, "Response code from copyMoveRename: " + responseCode);
			
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				is = connection.getInputStream();		
			}else
				is = connection.getErrorStream();
			
			BitcasaParseJSON parser = new BitcasaParseJSON();
			if (parser.readJsonStream(is)) {
				result = parser.mfiles;
			}
			else if (parser.mBitcasaError.getCode() > 0)
				throw new BitcasaServerException(parser.mBitcasaError);
			
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				//do nothing
			}
			else if (responseCode == HttpsURLConnection.HTTP_NOT_FOUND) {
				throw new BitcasaRequestErrorException(new BitcasaError(2002, "Folder does not exist.")); //2002
			} else if (responseCode == HttpsURLConnection.HTTP_FORBIDDEN) {
				switch(operationType) {
				case COPY:
					throw new BitcasaRequestErrorException(2006, "Cannot Copy. Specified location is read only.");
				case MOVE:
					throw new BitcasaRequestErrorException(2005, "Cannot Move. Specified location is read only.");
				case RENAME:
					throw new BitcasaRequestErrorException(2007, "Cannot Rename. Specified location is read only.");
				default:
					break;
				}
			} else if (responseCode == HttpsURLConnection.HTTP_CONFLICT) {
				switch(operationType) {
				case COPY:
					throw new BitcasaRequestErrorException(2018, "Name conflict on copy.");
				case MOVE:
					throw new BitcasaRequestErrorException(2017, "Name conflict on move.");
				case RENAME:
					throw new BitcasaRequestErrorException(2016, "Name conflict on rename.");
				default:
					break;
				}
			} else if (responseCode == HttpsURLConnection.HTTP_INTERNAL_ERROR) {
				throw new BitcasaRequestErrorException(2024, "Failed to save changes."); //2020,2021,2022,2023,2024,2025
			}else if (responseCode == HttpsURLConnection.HTTP_BAD_REQUEST) {
				throw new BitcasaRequestErrorException(2027, "Missing from/to/filename parameter.");	//2027,2028,2029,2030,2031
			}
			else {
				throw new BitcasaRequestErrorException(connection.getResponseMessage());
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException ioe) {
			if (ioe.getMessage().contains("authentication challenge"))
				throw new BitcasaAuthenticationException(1020, "Authorization code not recognized");
			else if (ioe != null)
				ioe.printStackTrace();
		} catch (BitcasaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (os != null) {
				os.close();
			}
			if (is != null) {
				is.close();
			}
			if (connection != null)
				connection.disconnect();
		}

		if (result != null && result.size() >= 1)
			return result.get(0);
		else
			return null;
    }
    
    public AccountInfo getAccountInfo() throws IOException, BitcasaException {
    	
    	AccountInfo ai = null;
		StringBuilder params = new StringBuilder();    	
		params.append(BitcasaRESTConstants.PARAM_ACCESS_TOKEN).append("=")
		.append(mAuthentication.getAccess_token());
		
		String url = mBitcasaRESTUtility.getRequestUrl(BitcasaRESTConstants.METHOD_USER, BitcasaRESTConstants.METHOD_PROFILE, params);
					
		
		Log.d(TAG, "getAccountInfo URL: " + url);
		HttpsURLConnection connection = null;
		InputStream is = null;
		JsonReader reader = null;
		BitcasaParseJSON parser = null;
		
		try {
			connection = (HttpsURLConnection) new URL(url)
					.openConnection();
			connection.setRequestMethod(BitcasaRESTConstants.REQUEST_METHOD_GET);
			
			// read response code
			final int responseCode = connection.getResponseCode();
			Log.d(TAG, "getList response The response code is: "
					+ responseCode);
			
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				is = connection.getInputStream();
			} 
			else
				is = connection.getErrorStream();
			
			parser = new BitcasaParseJSON();
			if (parser.readJsonStream(is))
				ai = parser.mAccountInfo;
			else if (parser.mBitcasaError.getCode() > 0)
				throw new BitcasaServerException(parser.mBitcasaError);	
			
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				//do nothing
			}
			else if (responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
				throw new BitcasaAuthenticationException(1020, "Authorization code not recognized");
			} 
			else
				throw new BitcasaRequestErrorException(Integer.toString(responseCode)); 
			
		} catch (IOException ioe) {
			if (ioe.getMessage().contains("authentication challenge"))
				throw new BitcasaAuthenticationException(1020, "Authorization code not recognized");
			else if (ioe != null)
				ioe.printStackTrace();
		} catch (Exception e) {
			if (e != null)
				e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
			
			if (is != null) {
				is.close();
			}
			
		}
		
		if (connection != null)
			connection.disconnect();
		
		return ai;
	}
    
    /**
     * Set Bicasa client Authorization Code
     * @param authentication_code
     */
    public void setAuthorizationCode(String authorization_code) {
    	mAuthentication.setAuthorization_code(authorization_code);
    }
    
    /**
     * Check if current Bitcasa client is still have the valid access token
     * @return
     */
    public boolean isLinked() {
    	if (mAuthentication.getAccess_token() == null)
    		return false;
    	else
    		return true;
    }
    
    /**
     * To unlink current Bitcasa client
     */
    public void unlink(){
    	mAuthentication.setAccess_token(null);
    }
    
    /**
     * set access token to bitcasa client 
     * @param accessToken
     */
    public void setAccessToken(String accessToken) {
    	mAuthentication.setAccess_token(accessToken);
    }
    
    private void validateFileFolderName(String name, FileType type) throws BitcasaFileSystemException {
    	
    	if (name == null || name.length() > 64 || name.startsWith(".") || name.matches(".*[<>:\"/\\|?*].*")) {
    		if (type == FileType.BITCASA_TYPE_FILE)
    			throw new BitcasaFileSystemException(new BitcasaError(2030, "file name is invalid"));
    		else
    			throw new BitcasaFileSystemException(new BitcasaError(2031, "folder name is invalid"));
    	}
    }
}
