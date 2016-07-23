package com.openfarmanager.android.model;

import android.content.res.Resources;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.model.exeptions.NetworkException;

/**
 * author: vnamashko
 */
public enum TaskStatusEnum {
    OK,
    CANCELED,
    ERROR_FILE_NOT_EXISTS,
    ERROR_COPY_TO_THE_SAME_FOLDER,
    ERROR_DELETE_FILE,
    ERROR_MOVE_FILE,
    ERROR_FILE_EXISTS,
    ERROR_RENAME_FILE,
    ERROR_WRONG_DESTINATION_FILE_NAME,
    ERROR_COPY,
    ERROR_EXPORT_AS,
    ERROR_UPDATE_INFO,
    ERROR_CAN_T_CREATE_DIRECTORY,
    ERROR_CREATING_ARCHIVE_FILES_TREE,
    ERROR_CREATE_BOOKMARK,
    ERROR_CREATE_ARCHIVE,
    ERROR_CREATE_DIRECTORY,
    ERROR_EXTRACTING_ARCHIVE_FILES_ENCRYPTION_PASSWORD_REQUIRED,
    ERROR_EXTRACTING_ARCHIVE_FILES,
    ERROR_NETWORK,
    ERROR_ACCESS_DENIED,
    ERROR_DROPBOX_SHARE,
    ERROR_FTP_DELETE_DIRECTORY,
    ERROR_STORAGE_PERMISSION_REQUIRED;

    private NetworkException mException;

    public NetworkException getNetworkErrorException() {
        return mException;
    }

    public static TaskStatusEnum createNetworkError(NetworkException exception) {
        TaskStatusEnum status = ERROR_NETWORK;
        ERROR_NETWORK.mException = exception;
        return status;
    }

    public static String getErrorString(TaskStatusEnum status) {
        return getErrorString(status, null);
    }

    public static String getErrorString(TaskStatusEnum status, String sub) {
        Resources res = App.sInstance.getResources();
        switch (status) {
            case CANCELED:
                return res.getString(R.string.canceled);
            case ERROR_FILE_NOT_EXISTS:
                return res.getString(R.string.error_cannot_copy_files, sub != null ? sub : "");
            case ERROR_COPY_TO_THE_SAME_FOLDER:
                return res.getString(R.string.error_cannot_copy_files_to_the_same_folder);
            case ERROR_DELETE_FILE: case ERROR_FTP_DELETE_DIRECTORY:
                return res.getString(R.string.error_cannot_delete_files);
            case ERROR_MOVE_FILE:
                return res.getString(R.string.error_cannot_move_files);
            case ERROR_FILE_EXISTS:
                return res.getString(R.string.error_file_already_exists);
            case ERROR_RENAME_FILE:
                return res.getString(R.string.error_cannot_rename_files);
            case ERROR_WRONG_DESTINATION_FILE_NAME:
                return res.getString(R.string.error_cannot_rename_files);
            case ERROR_COPY:
                return res.getString(R.string.error_cannot_copy_files, sub != null ? sub : "");
            case ERROR_CAN_T_CREATE_DIRECTORY:
                return res.getString(R.string.error_output_directory_doesnt_exists);
            case ERROR_CREATING_ARCHIVE_FILES_TREE:
                return res.getString(R.string.error_create_archive_files_tree);
            case ERROR_EXTRACTING_ARCHIVE_FILES:
                return res.getString(R.string.error_extract_archive_files);
            case ERROR_CREATE_BOOKMARK:
                return res.getString(R.string.error_create_bookmark);
            case ERROR_CREATE_ARCHIVE:
                return res.getString(R.string.error_create_archive);
            case ERROR_ACCESS_DENIED:
                return res.getString(R.string.error_access_denied);
            case ERROR_CREATE_DIRECTORY:
                return res.getString(R.string.error_cannot_create_file, sub != null ? sub : "");
            case ERROR_EXPORT_AS:
                return res.getString(R.string.error_export_as);
            case ERROR_UPDATE_INFO:
                return res.getString(R.string.error_update_file_info);
            case ERROR_DROPBOX_SHARE:
                return res.getString(R.string.error_dropbox_share_error);
            default:
                return "";
        }
    }
}
