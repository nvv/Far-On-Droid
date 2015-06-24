package com.mediafire.sdk.uploader;

import com.mediafire.sdk.MFApiException;
import com.mediafire.sdk.MFException;
import com.mediafire.sdk.MFSessionNotStartedException;

import java.io.IOException;

/**
 * Created by Chris on 5/18/2015.
 */
public interface MediaFireUploadHandler {

    /**
     * called when an upload has failed with an MFException. the upload may be retried, but likely will continue to fail
     * @param id id of upload
     * @param e MFException
     */
    public void uploadFailed(long id, MFException e);

    /**
     * called when an upload has failed with an MFApiException. the upload may be retried, depending on the error code
     * @param id id of upload
     * @param e MFApiException
     */
    public void uploadFailed(long id, MFApiException e);

    /**
     * called when an upload has failed with an MFSessionNotStartedException. the upload may be retried, but a session must be started.
     * @param id id of upload
     * @param e MFSessionNotStartedException
     */
    void uploadFailed(long id, MFSessionNotStartedException e);

    /**
     * called when an upload has failed with an IOException. the upload may be retried, but likely will continue to fail
     * @param id id of upload
     * @param e IOException
     */
    public void uploadFailed(long id, IOException e);

    /**
     * called when an upload has failed with an InterruptedException. the upload may be retried, depending on the cause of the interruption.
     * @param id id of upload
     * @param e InterruptedException
     */
    public void uploadFailed(long id, InterruptedException e);

    /**
     * called when a chunk of an upload has finished and the total progress has changed
     * @param id id of upload
     * @param percentFinished the approximate progress of the upload
     */
    public void uploadProgress(long id, double percentFinished);

    /**
     * called when an upload is finished
     * @param id id of upload
     * @param quickKey the quick key of the finished upload (can be null)
     * @param fileName the file name of the finished upload (can be null)
     */
    public void uploadFinished(long id, String quickKey, String fileName);

    /**
     * called when an upload is polling
     * @param id id of upload
     * @param statusCode the status code
     * @param description the description from the server
     */
    public void uploadPolling(long id, int statusCode, String description);
}
