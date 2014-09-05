package com.openfarmanager.android.filesystem.actions.network;

import android.support.v4.app.FragmentManager;
import com.dropbox.client2.exception.DropboxException;
import com.microsoft.live.LiveOperationException;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.actions.FileActionTask;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;
import com.yandex.disk.client.exceptions.WebdavException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbAuthException;

import static com.openfarmanager.android.model.TaskStatusEnum.*;

/**
 * @author Vlad Namashko
 */
public class DeleteFromNetworkTask extends NetworkActionTask {

    protected List<FileProxy> mItems;

    public DeleteFromNetworkTask(NetworkEnum networkType, FragmentManager fragmentManager, FileActionTask.OnActionListener listener,
                                 List<FileProxy> items) {
        // TODO: temporary
        super.mItems = new ArrayList<File>();

        mNetworkType = networkType;
        mItems = items;
        mFragmentManager = fragmentManager;
        mListener = listener;
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {

        NetworkApi api = getApi();
        totalSize = mItems.size();
        for (FileProxy file : mItems) {
            if (isCancelled()) {
                break;
            }
            try {
                api.delete(file);
                doneSize++;
                updateProgress();
            } catch (NullPointerException e) {
                return ERROR_FILE_NOT_EXISTS;
            } catch (DropboxException e) {
                return createNetworkError(NetworkException.handleNetworkException(e));
            } catch (LiveOperationException e) {
                return ERROR_DELETE_FILE;
            } catch (SmbAuthException e) {
                return createNetworkError(NetworkException.handleNetworkException(e));
            } catch (WebdavException e) {
                return createNetworkError(NetworkException.handleNetworkException(e));
            } catch (Exception e) {
                return ERROR_DELETE_FILE;
            }
        }

        return OK;
    }

}
