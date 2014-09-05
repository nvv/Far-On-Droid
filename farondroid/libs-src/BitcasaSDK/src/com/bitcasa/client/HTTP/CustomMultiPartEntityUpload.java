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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.protocol.HTTP;

import android.util.Log;
import com.bitcasa.client.ProgressListener;
import com.bitcasa.client.ProgressListener.ProgressAction;

public class CustomMultiPartEntityUpload extends MultipartEntity {

	private static final String TAG = CustomMultiPartEntityUpload.class.getSimpleName();
	
	private final ProgressListener mListener;
	private final String mFile;
	private final long mFileSize;
 
	public CustomMultiPartEntityUpload(final String file, final long fileSize, final ProgressListener listener) {
		super();
		mFile = file;
		mFileSize = fileSize;
		this.mListener = listener;
	}
 
	public CustomMultiPartEntityUpload(final HttpMultipartMode mode, final String file, final long fileSize, final ProgressListener listener) {
		super(mode, null, Charset.forName(HTTP.UTF_8));
		mFile = file;
		mFileSize = fileSize;
		this.mListener = listener;
	}
 
	public CustomMultiPartEntityUpload(HttpMultipartMode mode, final String boundary, final Charset charset, final String file, final long fileSize, final ProgressListener listener) {
		super(mode, boundary, charset);
		mFile = file;
		mFileSize = fileSize;
		this.mListener = listener;
	}
 
	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, this.mFile, this.mFileSize, this.mListener));
	}
 
	public static class CountingOutputStream extends FilterOutputStream {
 
		private final ProgressListener mListener;
		private final String mFileToUpload;
		private final long mFileSize;
		private long mTransferred;
		private long mProgressUpdateTimer = System.currentTimeMillis();
 
		public CountingOutputStream(final OutputStream out, final String file, final long fileSize, final ProgressListener listener) {
			super(out);
			mFileToUpload = file;
			mFileSize = fileSize;
			this.mListener = listener;
			this.mTransferred = 0;
		}
 
		public void write(byte[] b, int off, int len) throws IOException {
			if (!Thread.currentThread().isInterrupted()) {
				out.write(b, off, len);
				this.mTransferred += len;
				if (mListener != null && (System.currentTimeMillis() - mProgressUpdateTimer) > BitcasaRESTConstants.PROGRESS_UPDATE_INTERVAL) {
					Log.d(TAG, "file transfered so far: " + mTransferred + " out of total file size: " + mFileSize);
					int percentage = (int) (this.mTransferred * 100 / mFileSize);
					this.mListener.onProgressUpdate(mFileToUpload, percentage==0?1:percentage, ProgressAction.BITCASA_ACTION_UPLOAD);
					mProgressUpdateTimer = System.currentTimeMillis();
				}
			} else {
				this.mListener.canceled(mFileToUpload, ProgressAction.BITCASA_ACTION_UPLOAD);
				throw new IOException("Thread Interrupted");			
			}
		}
 
		public void write(int b) throws IOException {
			if (!Thread.currentThread().isInterrupted()) {
				out.write(b);
				this.mTransferred++;
				if (mListener != null && (System.currentTimeMillis() - mProgressUpdateTimer) > BitcasaRESTConstants.PROGRESS_UPDATE_INTERVAL) {
					Log.d(TAG, "file transfered so far: " + mTransferred + " out of total file size: " + mFileSize);
					int percentage = (int) (this.mTransferred * 100 / mFileSize);
					this.mListener.onProgressUpdate(mFileToUpload, percentage==0?1:percentage, ProgressAction.BITCASA_ACTION_UPLOAD);
					mProgressUpdateTimer = System.currentTimeMillis();
				}
			} else {
				this.mListener.canceled(mFileToUpload, ProgressAction.BITCASA_ACTION_UPLOAD);
				throw new IOException("Thread Interrupted");
			}
		}
	}
}
