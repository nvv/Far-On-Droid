package com.openfarmanager.android.filesystem.actions.network;

import android.support.v4.app.FragmentManager;
import com.dropbox.client2.exception.DropboxException;
import com.microsoft.live.LiveOperationException;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.actions.FileActionTask;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;
import com.yandex.disk.client.exceptions.WebdavException;

import java.io.File;
import java.util.ArrayList;

import static com.openfarmanager.android.model.TaskStatusEnum.*;

/**
 * @author Vlad Namashko
 */
public class CreateNewAtNetworkTask extends NetworkActionTask {

    protected String mDestination;

    public CreateNewAtNetworkTask(NetworkEnum networkType, FragmentManager fragmentManager, FileActionTask.OnActionListener listener,
                                 String destination) {
        // TODO: temporary
        super.mItems = new ArrayList<File>();

        mNetworkType = networkType;
        mDestination = destination;
        mFragmentManager = fragmentManager;
        mListener = listener;
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... params) {
        NetworkApi api = getApi();

        try {
            return api.createDirectory(mDestination) ? OK : ERROR_CREATE_DIRECTORY;
        } catch (DropboxException e) {
            return createNetworkError(NetworkException.handleNetworkException(e));
        } catch (WebdavException e) {
            return createNetworkError(NetworkException.handleNetworkException(e));
        } catch (LiveOperationException e) {
            return ERROR_CREATE_DIRECTORY;
        } catch (Exception e) {
            return ERROR_CREATE_DIRECTORY;
        }
    }

}
