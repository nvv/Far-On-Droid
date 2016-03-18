package com.openfarmanager.android.model.exeptions;

import com.dropbox.client2.exception.*;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.network.smb.SmbAPI;
import com.yandex.disk.client.exceptions.DuplicateFolderException;
import com.yandex.disk.client.exceptions.IntermediateFolderNotExistException;
import com.yandex.disk.client.exceptions.WebdavException;
import com.yandex.disk.client.exceptions.WebdavFileNotFoundException;
import com.yandex.disk.client.exceptions.WebdavForbiddenException;
import com.yandex.disk.client.exceptions.WebdavNotAuthorizedException;
import com.yandex.disk.client.exceptions.WebdavUserNotInitialized;

import java.io.IOException;
import java.net.SocketTimeoutException;

import jcifs.smb.SmbAuthException;

/**
 * @author Vlad Namashko
 */
public class NetworkException extends RuntimeException {

    private String mLocalizedError;
    private ErrorCause mErrorCause;

    public enum ErrorCause {
        Unlinked_Error, IO_Error, Cancel_Error, Server_error, Common_Error,
        FTP_Connection_Closed, Socket_Timeout, Access_Denied, Yandex_Disk_Error,
        Yandex_Disk_Not_Initialized_Error, Unknown_Error
    }

    public NetworkException() {}

    public NetworkException(String error, ErrorCause cause) {
        mLocalizedError = error;
        mErrorCause = cause;
    }

    public static NetworkException handleNetworkException(Exception e) {
        e.printStackTrace();
        NetworkException exception = new NetworkException();

        if (e instanceof DropboxUnlinkedException) {
            // happen either because you have not set an AccessTokenPair on your session, or because the user unlinked your app (revoked the access token pair).
            exception.mErrorCause = ErrorCause.Unlinked_Error;
            exception.mLocalizedError = getString(R.string.error_account_unlinked);

        } else if (e instanceof DropboxParseException || e instanceof DropboxIOException) {
            // 1) indicates there was trouble parsing a response from Dropbox.
            // 2) happens all the time, probably want to retry automatically.
            exception.mErrorCause = ErrorCause.IO_Error;
            exception.mLocalizedError = getString(R.string.error_io_error);

        } else if (e instanceof DropboxPartialFileException) {
            // canceled operation
            exception.mErrorCause = ErrorCause.Cancel_Error;
            exception.mLocalizedError = getString(R.string.error_canceled);
        } else if (e instanceof DropboxServerException) {
            DropboxServerException error = (DropboxServerException) e;

            // Server-side exception.  These are examples of what could happen,
            // but we don't do anything special with them here.
            exception.mErrorCause = ErrorCause.Server_error;
            switch (error.error) {
//                case DropboxServerException._304_NOT_MODIFIED:
//                case DropboxServerException._401_UNAUTHORIZED:
//                case DropboxServerException._403_FORBIDDEN:
//                case DropboxServerException._404_NOT_FOUND:
//                case DropboxServerException._406_NOT_ACCEPTABLE:
//                case DropboxServerException._415_UNSUPPORTED_MEDIA:
                case DropboxServerException._507_INSUFFICIENT_STORAGE:
                    exception.mLocalizedError = getString(R.string.error_network_quota);
                    break;
                default:
                    exception.mLocalizedError = error.reason;
                    break;
            }

        } else if (e instanceof DropboxException) {
            exception.mErrorCause = ErrorCause.Common_Error;
            exception.mLocalizedError = e.getMessage();
        } else if (e instanceof SocketTimeoutException) {
            exception.mErrorCause = ErrorCause.Socket_Timeout;
            exception.mLocalizedError = getString(R.string.error_socket_timeout_exception);
        } else if (e instanceof SmbAuthException) {
            if (SmbAPI.ACCESS_DENIED.equals(e.getMessage())) {
                exception.mErrorCause = ErrorCause.Access_Denied;
                exception.mLocalizedError = getString(R.string.error_access_denied);
            }
        } else if (e instanceof IOException && "FTPConnection closed".equals(e.getMessage())) {
            exception.mErrorCause = ErrorCause.FTP_Connection_Closed;
            exception.mLocalizedError = getString(R.string.error_ftp_connection_closed);
        } else if (e instanceof WebdavException) {
            exception.mErrorCause = ErrorCause.Yandex_Disk_Error;

            if (e instanceof WebdavFileNotFoundException) {
                exception.mLocalizedError = getString(R.string.error_file_not_found);
            } else if (e instanceof WebdavNotAuthorizedException) {
                exception.mErrorCause = ErrorCause.Yandex_Disk_Not_Initialized_Error;
                exception.mLocalizedError = getString(R.string.error_user_not_authorized);
            } else if (e instanceof WebdavUserNotInitialized) {
                exception.mErrorCause = ErrorCause.Yandex_Disk_Not_Initialized_Error;
                exception.mLocalizedError = getString(R.string.error_user_not_initialized);
            } else if (e instanceof WebdavForbiddenException) {
                exception.mLocalizedError = getString(R.string.error_forbidden);
            } else if (e instanceof DuplicateFolderException) {
                exception.mLocalizedError = getString(R.string.error_duplicated_folder);
            } else if (e instanceof IntermediateFolderNotExistException) {
                exception.mLocalizedError = getString(R.string.error_intermediate_folder_not_exist);
            } else {
                exception.mErrorCause = ErrorCause.Unknown_Error;
                exception.mLocalizedError = getString(R.string.error_unknown_unexpected_error);
            }
        } else {
            exception.mErrorCause = ErrorCause.Unknown_Error;
            exception.mLocalizedError = getString(R.string.error_unknown_unexpected_error);
        }

        return exception;
    }

    public String getLocalizedError() {
        return mLocalizedError;
    }

    public ErrorCause getErrorCause() {
        return mErrorCause;
    }

    private static String getString(int codeId) {
        return App.sInstance.getString(codeId);
    }

}
