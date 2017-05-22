package com.openfarmanager.android.filesystem.actions.network;

import com.microsoft.live.LiveOperationException;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.model.exeptions.FtpDirectoryDeleteException;
import com.openfarmanager.android.model.exeptions.NetworkException;
import com.yandex.disk.client.exceptions.WebdavException;

import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbAuthException;

import static com.openfarmanager.android.model.TaskStatusEnum.*;

/**
 * @author Vlad Namashko
 */
public class DeleteFromNetworkTask extends NetworkActionTask {

    protected List<FileProxy> mItems;

    public DeleteFromNetworkTask(BaseFileSystemPanel panel, List<FileProxy> items) {
        super(panel, new ArrayList<>());
        mItems = items;
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {

        NetworkApi api = getApi();
        mDoneSize = mItems.size();
        for (FileProxy file : mItems) {
            if (isCancelled()) {
                break;
            }
            try {
                api.delete(file);
                mDoneSize++;
                updateProgress();
            } catch (NullPointerException e) {
                return ERROR_FILE_NOT_EXISTS;
            } catch (LiveOperationException e) {
                return ERROR_DELETE_FILE;
            } catch (SmbAuthException e) {
                return createNetworkError(NetworkException.handleNetworkException(e));
            } catch (WebdavException e) {
                return createNetworkError(NetworkException.handleNetworkException(e));
            } catch (FtpDirectoryDeleteException e) { // special case
                return ERROR_FTP_DELETE_DIRECTORY;
            } catch (Exception e) {
                return ERROR_DELETE_FILE;
            }
        }

        return OK;
    }

}
